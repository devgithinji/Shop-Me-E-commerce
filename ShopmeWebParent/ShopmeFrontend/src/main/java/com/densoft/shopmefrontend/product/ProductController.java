package com.densoft.shopmefrontend.product;

import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.entity.Product;
import com.densoft.shopmefrontend.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/category/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/category/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias, @PathVariable("pageNum") Integer pageNum, Model model) {
        Category category = categoryService.getCategory(alias);
        if (category == null) {
            return "error/404";
        }
        List<Category> listCategoryParents = categoryService.getCategoryParents(category);
        Page<Product> productPage = productService.listByCategory(pageNum, category.getId());
        List<Product> products = productPage.getContent();

        long startCount = (long) (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > productPage.getTotalElements()) {
            model.addAttribute("pageTitle", category.getName());
            endCount = productPage.getTotalElements();
            model.addAttribute("listCategoryParents", listCategoryParents);
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", productPage.getTotalElements());

        model.addAttribute("pageTitle", category.getName());
        model.addAttribute("products", products);
        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("category", category);

        return "products_by_category";
    }


}
