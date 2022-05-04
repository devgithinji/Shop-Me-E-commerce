package com.densoft.shopmefrontend.product;

import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.entity.product.Product;
import com.densoft.shopmecommon.exception.CategoryNotFoundException;
import com.densoft.shopmecommon.exception.ProductNotFoundException;
import com.densoft.shopmefrontend.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
        try {
            Category category = categoryService.getCategory(alias);

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

            return "product/products_by_category";
        } catch (CategoryNotFoundException e) {
            return "error/404";
        }

    }

    @GetMapping("/product/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getShortName());
            return "product/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(@RequestParam("keyWord") String keyword,
                                  Model model) {
        return searchByPage(keyword, 1, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(
            @RequestParam(value = "keyWord", required = false) String keyword,
            @PathVariable("pageNum") int pageNum,
            Model model) {
        Page<Product> productPage = productService.search(keyword, pageNum);
        List<Product> products = productPage.getContent();

        long startCount = (long) (pageNum - 1) * ProductService.SEARCH_RESULT_PER_PAGE + 1;
        long endCount = startCount + ProductService.SEARCH_RESULT_PER_PAGE - 1;
        if (endCount > productPage.getTotalElements()) {
            endCount = productPage.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", productPage.getTotalElements());

        model.addAttribute("pageTitle", keyword + " - Search Result");

        model.addAttribute("keyWord", keyword);
        model.addAttribute("products", products);

        return "product/search_result";
    }
}
