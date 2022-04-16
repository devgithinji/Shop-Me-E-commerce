package com.densoft.shopmeAdmin.user.controller;

import com.densoft.shopmeAdmin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/check_email")
    public String checkUniqueEmail(@RequestParam(name = "id", required = false,defaultValue = "0") int id, @RequestParam("email") String email) {

        return userService.isEmailUnique(id == 0 ? null : id, email) ? "OK" : "Duplicated";
    }

}
