package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.Interfaces.IMenuService;
import com.group_one.food_delivery_app.models.MenuModel;
import com.group_one.food_delivery_app.repositories.CategoryRepository;
import com.group_one.food_delivery_app.repositories.MenuRepository;
import com.group_one.food_delivery_app.repositories.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/backend/menu")
@AllArgsConstructor
public class MenuController {
    private final IMenuService menuService;
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String index(Model model) {
        var menus = menuService.GetData();
        model.addAttribute("menus", menus);
        return "backend/menu/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("menu", new MenuModel());
        model.addAttribute("restaurants", restaurantRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("pageTitle", "Add New Menu Item");
        return "backend/menu/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        MenuModel menu = menuRepository.findById(id).orElse(null);
        if (menu == null) {
            return "redirect:/backend/menu";
        }
        model.addAttribute("menu", menu);
        model.addAttribute("restaurants", restaurantRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("pageTitle", "Edit Menu Item");
        return "backend/menu/edit";
    }

    @PostMapping("/store")
    public String store(@RequestParam("name") String name,
                        @RequestParam("price") double price,
                        @RequestParam("description") String description,
                        @RequestParam("image") String image,
                        @RequestParam("quantity") int quantity,
                        @RequestParam("restaurantId") int restaurantId,
                        @RequestParam(value = "categoryId", required = false) Integer categoryId,
                        @RequestParam(value = "id", required = false) Integer id) {
        MenuModel menu;
        if (id != null && id > 0) {
            menu = menuRepository.findById(id).orElse(new MenuModel());
        } else {
            menu = new MenuModel();
        }
        menu.setName(name);
        menu.setPrice(price);
        menu.setDescription(description);
        menu.setImage(image);
        menu.setQuantity(quantity);
        menu.setRestaurants(restaurantRepository.findById(restaurantId).orElse(null));
        if (categoryId != null) {
            menu.setCategories(categoryRepository.findById(categoryId).orElse(null));
        }
        menuRepository.save(menu);
        return "redirect:/backend/menu";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        menuRepository.deleteById(id);
        return "redirect:/backend/menu";
    }
}
