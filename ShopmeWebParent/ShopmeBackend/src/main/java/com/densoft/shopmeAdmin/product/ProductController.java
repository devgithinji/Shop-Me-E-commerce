package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.brand.BrandService;
import com.densoft.shopmeAdmin.category.CategoryNotFoundException;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;
    private final BrandService brandService;

    public ProductController(ProductService productService, BrandService brandService) {
        this.productService = productService;
        this.brandService = brandService;
    }

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

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductEnabledStatus(id, enabled);
            String status = enabled ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been " + status + " successfully");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setMainImage(fileName);
            Product savedProduct = productService.save(product);
            String uploadDir = "../product-images/" + savedProduct.getId();
            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        } else {
            productService.save(product);
        }
        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
        return "redirect:/products";
    }

    @GetMapping("products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
//            String categoryDir = "../category-images/" + id;
//            FileUpload.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The Product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException productNotFoundException) {
            redirectAttributes.addFlashAttribute("message", productNotFoundException.getMessage());
        }

        return "redirect:/products";
    }
}
