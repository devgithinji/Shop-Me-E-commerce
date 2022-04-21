package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmeAdmin.category.CategoryNotFoundException;
import com.densoft.shopmeAdmin.category.CategoryService;
import com.densoft.shopmeAdmin.category.exporter.CategoryCSVExporter;
import com.densoft.shopmeAdmin.user.UserService;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/brands")
    public String brandsList(Model model) {
        return listByPage(1, model, "name", "asc", null);
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, @RequestParam(value = "sortField", defaultValue = "name") String sortField, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "keyWord", required = false) String keyWord) {
        Page<Brand> page = brandService.listByPage(pageNum, sortField, sortDir, keyWord);
        List<Brand> brands = page.getContent();
        long startCount = (long) (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
        long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equalsIgnoreCase("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("brands", brands);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyWord", keyWord);
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


    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);
            Brand savedBrand = brandService.save(brand);
            String uploadDir = "../brand-logos/" + savedBrand.getId();
            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        } else {
            brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message", "Brand saved successfully");
        return "redirect:/brands";
    }


    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Brand brand = brandService.get(id);
            List<Category> categories = categoryService.listCategoriesUsedInForm();
            model.addAttribute("brand", brand);
            model.addAttribute("categories", categories);
            model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");

            return "brands/brand_form";

        } catch (BrandNotFoundException brandNotFoundException) {
            redirectAttributes.addFlashAttribute("message", brandNotFoundException.getMessage());
            return "redirect:/brands";
        }

    }


    @GetMapping("brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            brandService.delete(id);
            String brandDir = "../brand-logos/" + id;
            FileUpload.removeDir(brandDir);
            redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully");
        } catch (BrandNotFoundException brandNotFoundException) {
            redirectAttributes.addFlashAttribute("message", brandNotFoundException.getMessage());
        }

        return "redirect:/brands";
    }

    @GetMapping("/brands/export/csv")
    public void exportToCSV(HttpServletResponse response) throws Exception {
        List<Brand> brandList = brandService.listAll();
        BrandCSVExporter brandCSVExporter = new BrandCSVExporter();
        brandCSVExporter.export(brandList, response);
    }
}
