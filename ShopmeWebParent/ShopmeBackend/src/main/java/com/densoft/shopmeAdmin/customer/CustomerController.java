package com.densoft.shopmeAdmin.customer;

import com.densoft.shopmeAdmin.setting.country.CountryRepository;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.Product;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import com.densoft.shopmecommon.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CountryRepository countryRepository;


    @GetMapping("/customers")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "firstName", "asc", null);
    }


    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model, @RequestParam(value = "sortField", defaultValue = "firstName") String sortField, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, @RequestParam(value = "keyWord", required = false) String keyWord) {


        Page<Customer> page = customerService.listByPage(pageNum, sortField, sortDir, keyWord);
        List<Customer> customers = page.getContent();
        long startCount = (long) (pageNum - 1) * CustomerService.CUSTOMERS_PER_PAGE + 1;
        long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE - 1;

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
        model.addAttribute("customers", customers);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyWord", keyWord);

        return "customer/customers";
    }


    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.getCustomer(id);
            List<Country> countries = countryRepository.findAllByOrderByNameAsc();
            model.addAttribute("pageTitle", "Edit Customer (ID: " + id + ")");
            model.addAttribute("countries", countries);

            model.addAttribute("customer", customer);

            return "customer/customer_form";

        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }

    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, RedirectAttributes redirectAttributes) {
        customerService.save(customer);
        redirectAttributes.addFlashAttribute("message", customer.getId() != null ? "Customer ID: " + customer.getId() + " updated successfully" : "Customer saved successfully");
        return "redirect:/customers";
    }

    @GetMapping("/customers/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.getCustomer(id);
            model.addAttribute("customer", customer);
            return "customer/customer_detail_modal";

        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }

    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        try {
            customerService.updateCustomerEnabledStatus(id, enabled);
            String status = enabled ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("message", "The customer ID " + id + " has been " + status + " successfully");
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The Customer ID " + id + " has been deleted successfully");
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/customers";
    }
}
