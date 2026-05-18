package com.example.food_delivery_app.backend_user.services;

import com.example.food_delivery_app.models.FreeDeliveryAssignmentModel;
import com.example.food_delivery_app.repositories.FreeDeliveryAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FreeDeliveryAssignmentService {

    @Autowired
    private FreeDeliveryAssignmentRepository freeDeliveryAssignmentRepository;

    public Page<FreeDeliveryAssignmentModel> getAllAssignments(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return freeDeliveryAssignmentRepository.findAllByKeyword(keyword, pageable);
    }

    public Optional<FreeDeliveryAssignmentModel> getAssignmentById(Long id) {
        return freeDeliveryAssignmentRepository.findById(id);
    }

    public FreeDeliveryAssignmentModel createAssignment(FreeDeliveryAssignmentModel assignment) {
        validateAssignment(assignment);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        return freeDeliveryAssignmentRepository.save(assignment);
    }

    public FreeDeliveryAssignmentModel updateAssignment(Long id, FreeDeliveryAssignmentModel assignmentDetails) {
        Optional<FreeDeliveryAssignmentModel> existingAssignmentOpt = freeDeliveryAssignmentRepository.findById(id);

        if (existingAssignmentOpt.isPresent()) {
            FreeDeliveryAssignmentModel existingAssignment = existingAssignmentOpt.get();

            validateAssignment(assignmentDetails);

            existingAssignment.setRestaurant(assignmentDetails.getRestaurant());
            existingAssignment.setMenuItem(assignmentDetails.getMenuItem());
            existingAssignment.setAssignmentType(assignmentDetails.getAssignmentType());
            existingAssignment.setStatus(assignmentDetails.getStatus());
            existingAssignment.setMinOrderAmount(assignmentDetails.getMinOrderAmount());
            existingAssignment.setStartDate(assignmentDetails.getStartDate());
            existingAssignment.setEndDate(assignmentDetails.getEndDate());
            existingAssignment.setNotes(assignmentDetails.getNotes());
            existingAssignment.setUpdatedAt(LocalDateTime.now());

            return freeDeliveryAssignmentRepository.save(existingAssignment);
        } else {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
    }

    public void deleteAssignment(Long id) {
        if (freeDeliveryAssignmentRepository.existsById(id)) {
            freeDeliveryAssignmentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
    }

    public FreeDeliveryAssignmentModel toggleStatus(Long id) {
        Optional<FreeDeliveryAssignmentModel> assignmentOpt = freeDeliveryAssignmentRepository.findById(id);
        if (assignmentOpt.isPresent()) {
            FreeDeliveryAssignmentModel assignment = assignmentOpt.get();
            if (assignment.getStatus() == FreeDeliveryAssignmentModel.AssignmentStatus.ACTIVE) {
                assignment.setStatus(FreeDeliveryAssignmentModel.AssignmentStatus.INACTIVE);
            } else {
                assignment.setStatus(FreeDeliveryAssignmentModel.AssignmentStatus.ACTIVE);
            }
            assignment.setUpdatedAt(LocalDateTime.now());
            return freeDeliveryAssignmentRepository.save(assignment);
        } else {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
    }

    public List<FreeDeliveryAssignmentModel> getAssignmentsByRestaurant(Integer restaurantId) {
        return freeDeliveryAssignmentRepository.findByRestaurantResId(restaurantId);
    }

    public List<FreeDeliveryAssignmentModel> getAssignmentsByMenuItem(Integer menuItemId) {
        return freeDeliveryAssignmentRepository.findByMenuItemId(menuItemId);
    }

    public AssignmentStats getStats() {
        List<FreeDeliveryAssignmentModel> all = freeDeliveryAssignmentRepository.findAll();
        long total = all.size();
        long active = all.stream().filter(a -> a.getStatus() == FreeDeliveryAssignmentModel.AssignmentStatus.ACTIVE).count();
        long restaurantType = all.stream().filter(a -> a.getAssignmentType() == FreeDeliveryAssignmentModel.AssignmentType.RESTAURANT).count();
        long menuItemType = all.stream().filter(a -> a.getAssignmentType() == FreeDeliveryAssignmentModel.AssignmentType.MENU_ITEM).count();
        
        return new AssignmentStats(total, active, restaurantType, menuItemType);
    }

    private void validateAssignment(FreeDeliveryAssignmentModel assignment) {
        if (assignment.getAssignmentType() == FreeDeliveryAssignmentModel.AssignmentType.RESTAURANT) {
            if (assignment.getRestaurant() == null) {
                throw new RuntimeException("Restaurant is required for RESTAURANT assignment type");
            }
            assignment.setMenuItem(null);
        } else if (assignment.getAssignmentType() == FreeDeliveryAssignmentModel.AssignmentType.MENU_ITEM) {
            if (assignment.getMenuItem() == null) {
                throw new RuntimeException("Menu item is required for MENU_ITEM assignment type");
            }
            assignment.setRestaurant(null);
        }
    }

    public static class AssignmentStats {
        public final long total;
        public final long active;
        public final long restaurantType;
        public final long menuItemType;

        public AssignmentStats(long total, long active, long restaurantType, long menuItemType) {
            this.total = total;
            this.active = active;
            this.restaurantType = restaurantType;
            this.menuItemType = menuItemType;
        }
    }
}
