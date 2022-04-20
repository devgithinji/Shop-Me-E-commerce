package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmeAdmin.category.CategoryService;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/brands")
    public String brandsList(Model model) {
        List<Brand> brands = brandService.listBrands();
        model.addAttribute("brands", brands);
        return "brands/brands";

    }

    @GetMapping("/brands/new")
    public String newBrand(Model model) {
        List<Category> categories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("brand", new Brand());
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Create new brand");
        return "brands/brand_form";
    }
}
