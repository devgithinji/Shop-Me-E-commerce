package com.densoft.shopmeAdmin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
    @Autowired
    private CustomerService customerService;


    @PostMapping("/customers/check_unique")
    public String checkUnique(@RequestParam(value = "id", required = false) Integer id, @RequestParam("email") String email) {
        return customerService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }

}
