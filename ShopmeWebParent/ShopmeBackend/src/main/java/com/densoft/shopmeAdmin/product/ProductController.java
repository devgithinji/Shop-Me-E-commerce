package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.brand.BrandService;
import com.densoft.shopmeAdmin.category.CategoryService;
import com.densoft.shopmeAdmin.security.CustomUserDetails;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
;

import static com.densoft.shopmeAdmin.product.ProductSaveHelper.*;

@Controller
public class ProductController {
    private final ProductService productService;
    private final BrandService brandService;

    private final CategoryService categoryService;


    public ProductController(ProductService productService, BrandService brandService, CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "name", "asc", null, 0);
    }


    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") int pageNum,
            Model model, @RequestParam(value = "sortField", defaultValue = "name") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "keyWord", required = false) String keyWord,
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        System.out.println("selected category id: " + categoryId);

        Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyWord, categoryId);
        List<Category> categoryList = categoryService.listCategoriesUsedInForm();
        List<Product> products = page.getContent();
        long startCount = (long) (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equalsIgnoreCase("asc") ? "desc" : "asc";

        if (categoryId != null) model.addAttribute("categoryId", categoryId);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("products", products);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("listCategories", categoryList);
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
        model.addAttribute("numberOfExistingExtraImages", 0);
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
    public String saveProduct(
            Product product,
            @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile,
            @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiPartFiles,
            @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
            @RequestParam(name = "detailNames", required = false) String[] detailNames,
            @RequestParam(name = "detailValues", required = false) String[] detailValues,
            @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
            @RequestParam(name = "imageNames", required = false) String[] imageNames,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        if (userDetails.hasRole("SalesPerson")) {
            try {
                productService.saveProductPrice(product);
                redirectAttributes.addFlashAttribute("message", "The product saved successfully");
            } catch (ProductNotFoundException e) {
                redirectAttributes.addFlashAttribute("message", e.getMessage());
            }
            return "redirect:/products";
        }

        setMainImageName(mainImageMultipartFile, product);
        setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultiPartFiles, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);
        saveUploadedImages(mainImageMultipartFile, extraImageMultiPartFiles, savedProduct);
        deleteExtraImagesWereRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
        return "redirect:/products";
    }

    @GetMapping("products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;
            FileUpload.removeDir(productExtraImagesDir);
            FileUpload.removeDir(productImagesDir);
            redirectAttributes.addFlashAttribute("message", "The Product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException productNotFoundException) {
            redirectAttributes.addFlashAttribute("message", productNotFoundException.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable("id") Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");

            List<Brand> brands = brandService.listAll();
            model.addAttribute("brands", brands);
            model.addAttribute("product", product);
            Integer numberOfExistingExtraImages = product.getImages().size();
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }

    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(
            @PathVariable("id") Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }

    }

}
