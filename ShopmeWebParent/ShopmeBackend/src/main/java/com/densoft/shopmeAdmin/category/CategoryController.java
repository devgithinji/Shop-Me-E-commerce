package com.densoft.shopmeAdmin.category;

import com.densoft.shopmeAdmin.category.exporter.CategoryCSVExporter;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, Model model) {
        return listByPage(1, sortDir, null, model);
    }

    @GetMapping("categories/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") int pageNum,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "keyWord", required = false) String keyWord,
            Model model) {

        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

        List<Category> categories = categoryService.listByPage(categoryPageInfo, pageNum, sortDir, keyWord);

        long startCount = (long) (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > categoryPageInfo.getTotalElements()) {
            endCount = categoryPageInfo.getTotalElements();
        }
        model.addAttribute("categories", categories);
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
        model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("moduleURL", "/categories");
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
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);
            Category savedCategory = categoryService.save(category);
            String uploadDir = "../category-images/" + savedCategory.getId();
            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }

        redirectAttributes.addFlashAttribute("message", "Category saved successfully");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {

            Category category = categoryService.get(id);
            List<Category> categories = categoryService.listCategoriesUsedInForm();
            model.addAttribute("category", category);
            model.addAttribute("listCategories", categories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");
            return "categories/category_form";

        } catch (CategoryNotFoundException categoryNotFoundException) {
            redirectAttributes.addFlashAttribute("message", categoryNotFoundException.getMessage());
            return "redirect:/categories";
        }

    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategoryEnabledStatus(id, enabled);
            String status = enabled ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been " + status + " successfully");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            String categoryDir = "../category-images/" + id;
            FileUpload.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
        } catch (CategoryNotFoundException categoryNotFoundException) {
            redirectAttributes.addFlashAttribute("message", categoryNotFoundException.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws Exception {
        List<Category> categoryList = categoryService.listCategoriesUsedInForm();
        CategoryCSVExporter categoryCSVExporter = new CategoryCSVExporter();
        categoryCSVExporter.export(categoryList, response);
    }
}
