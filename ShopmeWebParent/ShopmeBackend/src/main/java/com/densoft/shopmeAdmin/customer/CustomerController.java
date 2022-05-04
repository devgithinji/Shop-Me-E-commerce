package com.densoft.shopmeAdmin.customer;

import com.densoft.shopmeAdmin.paging.PagingAndSortingHelper;
import com.densoft.shopmeAdmin.paging.PagingAndSortingParam;
import com.densoft.shopmeAdmin.setting.country.CountryRepository;
import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CountryRepository countryRepository;


    @GetMapping("/customers")
    public String listFirstPage(Model model) {
        return "redirect:/customers/page/1?sortField=firstName&sortDir=asc";
    }


    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "customers", moduleURL = "/customers") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        customerService.listByPage(pageNum, helper);
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


    @GetMapping("/customers/export/csv")
    public void exportToCSV(HttpServletResponse response) throws Exception {
        List<Customer> customerList = customerService.listAll();
        CustomerCSVExporter customerCSVExporter = new CustomerCSVExporter();
        customerCSVExporter.export(customerList, response);
    }
}
