package com.densoft.shopmeAdmin.user;

import com.densoft.shopmeAdmin.security.CustomUserDetails;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;


    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) throws UserNotFoundException {
        String email = customUserDetails.getUsername();
        User user = userService.getByEmail(email);
        model.addAttribute("user", user);
        return "account_form";
    }

    @PostMapping("/account/update")
    public String saveDetails(User user, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.updateAccount(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.updateAccount(user);
        }

        customUserDetails.setFirstName(user.getFirstName());
        customUserDetails.setLastName(user.getLastName());

        redirectAttributes.addFlashAttribute("message", "Your account details updated successfully");

        return "redirect:/account";
    }
}
