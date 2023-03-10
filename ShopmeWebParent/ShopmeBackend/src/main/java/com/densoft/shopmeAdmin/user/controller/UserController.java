package com.densoft.shopmeAdmin.user.controller;

import com.densoft.shopmeAdmin.AmazonS3Util;
import com.densoft.shopmeAdmin.paging.PagingAndSortingHelper;
import com.densoft.shopmeAdmin.paging.PagingAndSortingParam;
import com.densoft.shopmeAdmin.user.exception.UserNotFoundException;
import com.densoft.shopmeAdmin.user.UserService;
import com.densoft.shopmeAdmin.user.export.UserCsvExporter;
import com.densoft.shopmeAdmin.user.export.UserExcelExporter;
import com.densoft.shopmeAdmin.user.export.UserPdfExporter;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Role;
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
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/users")
    public String getUsers() {

        return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "users", moduleURL = "/users") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {
        userService.listByPage(pageNum, helper);

        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> roles = userService.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "User saved successfully");

        return getRedirectToAffectedUser(user);
    }

    private String getRedirectToAffectedUser(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];

        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyWord=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUser(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            List<Role> roles = userService.listRoles();
            model.addAttribute("roles", roles);

            return "users/user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            //remove user photo
            String userPhotosDir = "user-photos/" + id;
            AmazonS3Util.removeFolder(userPhotosDir);
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserEnabledStatus(id, enabled);
            String status = enabled ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + "has been " + status + " successfully");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> users = userService.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(users, response);

    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> users = userService.listAll();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(users, response);

    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> users = userService.listAll();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(users, response);

    }
}
