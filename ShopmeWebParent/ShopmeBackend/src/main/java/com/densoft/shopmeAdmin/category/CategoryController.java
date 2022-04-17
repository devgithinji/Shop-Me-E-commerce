package com.densoft.shopmeAdmin.category;

import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        category.setImage(fileName);
        Category savedCategory = categoryService.save(category);
        String uploadDir = "../category-images/" + savedCategory.getId();
        FileUpload.saveFile(uploadDir, fileName, multipartFile);
        redirectAttributes.addFlashAttribute("message", "Category saved successfully");
        return "redirect:/categories";
    }
}
