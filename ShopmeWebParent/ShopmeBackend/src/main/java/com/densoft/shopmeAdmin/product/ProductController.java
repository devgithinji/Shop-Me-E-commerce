package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.brand.BrandService;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    @GetMapping("/products")
    public String listAll(Model model) {
        List<Product> productList = productService.listAll();
        model.addAttribute("products", productList);
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> brands = brandService.listAll();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);
        model.addAttribute("brands", brands);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Create New Product");
        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product) {
        System.out.println("Product name: " + product.getName());
        System.out.println("Brand ID: " + product.getBrand().getId());
        System.out.println("Category Id: " + product.getCategory().getId());
        return "redirect:/products";
    }
}
