package com.densoft.shopmeAdmin.category;

import com.densoft.shopmecommon.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.categoriesList();
        categories.forEach(category -> {
            System.out.println(category.getName());
        });
        model.addAttribute("categories", categories);
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> categoryLIst = categoryService.listCategoriesUsedInForm();
        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", categoryLIst);
        model.addAttribute("pageTitle", "Create new category");
        return "categories/category_form";
    }
}
