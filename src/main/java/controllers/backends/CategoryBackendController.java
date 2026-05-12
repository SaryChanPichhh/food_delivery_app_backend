package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.Interfaces.ICategoryService;
import com.group_one.food_delivery_app.models.CategoryModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/backend/category")
@AllArgsConstructor
public class CategoryBackendController {
    private final ICategoryService categoryService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categories", categoryService.GetData());
        return "backend/category/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("category", new CategoryModel());
        model.addAttribute("pageTitle", "Add New Category");
        return "backend/category/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        CategoryModel category = categoryService.findById(id);
        if (category == null) {
            return "redirect:/backend/category";
        }
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Edit Category");
        return "backend/category/edit";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute CategoryModel category) {
        if (category.getCateId() > 0) {
            categoryService.UpdateData(category);
        } else {
            categoryService.AddData(category);
        }
        return "redirect:/backend/category";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        CategoryModel category = categoryService.findById(id);
        if (category != null) {
            categoryService.Delete(category);
        }
        return "redirect:/backend/category";
    }
}
