package com.example.food_delivery_app.services;

import com.example.food_delivery_app.Interfaces.ISaleService;
import com.example.food_delivery_app.repositories.*;
import com.example.food_delivery_app.utils.enums.PaymentMethod;
import com.example.food_delivery_app.dtos.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SaleService implements ISaleService {
    private final SaleRepository _saleRepository;
    private final CartRepository _cartRepository;
    private final MenuRepository _menuRepository;
    private final UserRepository _userRepository;
    private final RestaurantRepository _restaurantRepository;
    private final CouponRepository _couponRepository;
    private final ExchangeRateRepository _exchangeRateRepository;
    private final DeliveryRepository _deliveryRepository;

    @Override
    public List<RestaurantResponseDto> getRestaurantInfo(int userId) {
        var list = _saleRepository.getRestaurantInfo(userId);
        return list;
    }

    @Override
    @jakarta.transaction.Transactional
    public List<SaleHeaderModel> getAllSaleOrderByUserId(int userId) {
        return _saleRepository.findAllByUserId(userId);
    }

    @Override
    @jakarta.transaction.Transactional
    public SaleHeaderModel getSaleOrderByUserIdAndId(int userId, int id) {
        return _saleRepository.getSaleOrderByUserIdAndId(userId, id);
    }

    @Override
    @Transactional
    public void addToCart(int userId, int menuId, int resId) {
        // 1. Fetch or create Cart (SaleHeaderModel with invoiceType = "CART")
        SaleHeaderModel cart = _saleRepository.findByUser_IdAndInvoiceTypeAndStatusTrue(userId, "ORDER");

        if (cart == null) {
            cart = new SaleHeaderModel();
            cart.setInvoiceType("ORDER");
            cart.setStatus(true);
            cart.setCreatedAt(LocalDate.now());
            cart.setTotal(0.0);

            UserModel user = _userRepository.findById(userId).orElseThrow();
            cart.setUser(user);

            cart = _saleRepository.save(cart);
        }

        // Set or update current exchange rate
        var defaultRateOpt = _exchangeRateRepository.findByDefaultRateTrue();
        if (defaultRateOpt.isPresent()) {
            cart.setExchangeRate(defaultRateOpt.get().getRate());
            cart.setExchangeRateModel(defaultRateOpt.get());
        } else {
            cart.setExchangeRate(4000.0); // Fallback
        }

        // 2. Add or update SaleDetailModel
        MenuModel menu = _menuRepository.findById(menuId).orElseThrow();
        RestaurantModel restaurant = _restaurantRepository.findById(resId).orElseThrow();

        // Calculate discounted price if applicable
        double originalPrice = menu.getPrice();
        double discountedPrice = originalPrice;

        var activeCouponOpt = _couponRepository.findActiveCouponByRestaurantId(resId, LocalDateTime.now());
        if (activeCouponOpt.isPresent()) {
            CouponModel coupon = activeCouponOpt.get();
            if (coupon.getDiscountType() == CouponModel.DiscountType.PERCENTAGE) {
                discountedPrice = originalPrice - (originalPrice * coupon.getDiscountValue() / 100.0);
            } else if (coupon.getDiscountType() == CouponModel.DiscountType.FIXED_AMOUNT) {
                discountedPrice = Math.max(0, originalPrice - coupon.getDiscountValue());
            }
        }

        SaleDetailModel detail = _cartRepository.findBySaleHeader_IdAndItemCode(cart.getId(),
                String.valueOf(menu.getId()));

        if (detail != null) {
            // Item exists in cart, increment quantity
            detail.setQty(detail.getQty() + 1);
            // Use the current discounted price (in case it changed or for consistency)
            detail.setSalePrice(discountedPrice);
            detail.setTotal(detail.getQty() * detail.getSalePrice());
            _cartRepository.save(detail);
        } else {
            // New item to cart
            detail = new SaleDetailModel();
            detail.setSaleHeader(cart);
            detail.setItemCode(String.valueOf(menu.getId()));
            detail.setItemDesc(menu.getName());
            detail.setQty(1);
            detail.setSalePrice(discountedPrice);
            detail.setTotal(discountedPrice);
            detail.setStatus(true);
            detail.setCreatedAt(LocalDate.now());
            detail.setRestaurant(restaurant);

            _cartRepository.save(detail);
        }

        // 3. Update Header Total
        // Re-calculate total from all items to ensure accuracy
        List<SaleDetailModel> allDetails = _cartRepository.findAllBySaleHeader_Id(cart.getId());
        double newTotal = allDetails.stream().mapToDouble(SaleDetailModel::getTotal).sum();

        cart.setTotal(newTotal);
        _saleRepository.save(cart);
    }

    @Override
    public SaleHeaderModel getActiveCart(int userId) {
        return _saleRepository.findByUser_IdAndInvoiceTypeAndStatusTrue(userId, "ORDER");
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(int userId, int detailId, int changeAmount) {
        SaleHeaderModel cart = getActiveCart(userId);
        if (cart == null)
            return;

        SaleDetailModel detail = _cartRepository.findById(detailId).orElse(null);
        if (detail == null || detail.getSaleHeader().getId() != cart.getId())
            return;

        int newQty = detail.getQty() + changeAmount;
        if (newQty <= 0) {
            removeFromCart(userId, detailId);
            return;
        }

        // Adjust differences
        double oldTotal = detail.getTotal();
        detail.setQty(newQty);
        double newDetailTotal = newQty * detail.getSalePrice();
        detail.setTotal(newDetailTotal);
        _cartRepository.save(detail);

        double newCartTotal = cart.getTotal() - oldTotal + newDetailTotal;
        if (newCartTotal < 0.01)
            newCartTotal = 0.0;
        cart.setTotal(newCartTotal);
        _saleRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(int userId, int detailId) {
        SaleHeaderModel cart = getActiveCart(userId);
        if (cart == null)
            return;

        SaleDetailModel detail = _cartRepository.findById(detailId).orElse(null);
        if (detail == null || detail.getSaleHeader().getId() != cart.getId())
            return;

        double newCartTotal = cart.getTotal() - detail.getTotal();
        if (newCartTotal < 0.01)
            newCartTotal = 0.0;
        cart.setTotal(newCartTotal);
        _saleRepository.save(cart);

        _cartRepository.delete(detail);
    }

    @Override
    @Transactional
    public void checkoutCart(int userId, PaymentMethod paymentMethod, Long deliveryId) {
        SaleHeaderModel cart = getActiveCart(userId);
        if (cart != null) {
            cart.setInvoiceType("PAID");
            cart.setPaymentMethod(paymentMethod != null ? paymentMethod.name() : "CASH");

            // Set Delivery Person
            if (deliveryId != null) {
                DeliveryModel delivery = _deliveryRepository.findById(deliveryId).orElse(null);
                cart.setDelivery(delivery);
            }

            // Calculate and store total commission
            double totalCommission = 0;
            if (cart.getSaleDetails() != null) {
                for (SaleDetailModel detail : cart.getSaleDetails()) {
                    if (detail.getRestaurant() != null) {
                        double rate = detail.getRestaurant().getCommissionRate() / 100.0;
                        totalCommission += (detail.getSalePrice() * detail.getQty()) * rate;
                    }
                }
            }
            cart.setCommissionAmount(totalCommission);

            // Refresh and store current exchange rate from model
            var defaultRateOpt = _exchangeRateRepository.findByDefaultRateTrue();
            if (defaultRateOpt.isPresent()) {
                cart.setExchangeRate(defaultRateOpt.get().getRate());
                cart.setExchangeRateModel(defaultRateOpt.get());
            }

            _saleRepository.save(cart);
        }
    }

    // Existing method now delegates to the new one with default CASH
    @Override
    @Transactional
    public void checkoutCart(int userId, Long deliveryId) {
        checkoutCart(userId, PaymentMethod.CASH, deliveryId);
    }
}
