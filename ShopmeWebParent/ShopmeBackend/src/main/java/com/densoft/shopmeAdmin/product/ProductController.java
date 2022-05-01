package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.brand.BrandService;
import com.densoft.shopmeAdmin.category.CategoryService;
import com.densoft.shopmeAdmin.customer.CustomerCSVExporter;
import com.densoft.shopmeAdmin.paging.PagingAndSortingHelper;
import com.densoft.shopmeAdmin.paging.PagingAndSortingParam;
import com.densoft.shopmeAdmin.security.CustomUserDetails;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.Product;
import com.densoft.shopmecommon.exception.ProductNotFoundException;
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

import javax.servlet.http.HttpServletResponse;
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
        return "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";
    }


    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "products", moduleURL = "/products") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum,
            Model model,
            @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {


        productService.listByPage(pageNum, helper, categoryId);

        List<Category> categoryList = categoryService.listCategoriesUsedInForm();

        if (categoryId != null) model.addAttribute("categoryId", categoryId);
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

        if (!userDetails.hasRole("Admin") && !userDetails.hasRole("Editor")) {
            if (userDetails.hasRole("Salesperson")) {
                try {
                    productService.saveProductPrice(product);
                    redirectAttributes.addFlashAttribute("message", "The product saved successfully");
                } catch (ProductNotFoundException e) {
                    redirectAttributes.addFlashAttribute("message", e.getMessage());
                }
                return "redirect:/products";
            }
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
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Product product = productService.get(id);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            List<Brand> brands = brandService.listAll();

            boolean isReadOnlyForSalesPerson = false;

            if (!userDetails.hasRole("Admin") && !userDetails.hasRole("Editor")) {
                if (userDetails.hasRole("Salesperson")) {
                    isReadOnlyForSalesPerson = true;
                }
            }

            model.addAttribute("isReadOnlyForSalesPerson", isReadOnlyForSalesPerson);
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


    @GetMapping("/products/export/csv")
    public void exportToCSV(HttpServletResponse response) throws Exception {
        List<Product> productList = productService.listAll();
        ProductCSVExporter productCSVExporter = new ProductCSVExporter();
        productCSVExporter.export(productList, response);
    }

}
