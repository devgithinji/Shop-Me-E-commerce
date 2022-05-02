package com.densoft.shopmefrontend.customer;

import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import com.densoft.shopmefrontend.Utility;
import com.densoft.shopmefrontend.security.CustomerOAuth2User;
import com.densoft.shopmefrontend.security.CustomerUserDetails;
import com.densoft.shopmefrontend.setting.EmailSettingBag;
import com.densoft.shopmefrontend.setting.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> listAllCountries = customerService.listAllCountries();
        model.addAttribute("countries", listAllCountries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        sendVerificationEmail(request, customer);
        model.addAttribute("pageTitle", "Registration Succeeded!");
        return "register/register_success";
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSetting = settingsService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSetting);
        String toAddress = customer.getEmail();
        String subject = emailSetting.getCustomerVerifySubject();
        String content = emailSetting.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", customer.getFullName());
        String verifyUrl = Utility.generateSiteUrl(request) + "/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[url]]", verifyUrl);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {
        boolean verified = customerService.verify(code);
        return "register/" + (verified ? "verify_success" : "verify_fail");
    }

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request) {
        List<Country> listAllCountries = customerService.listAllCountries();
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        Customer customer = customerService.getCustomerByEmail(email);
        model.addAttribute("customer", customer);
        model.addAttribute("countries", listAllCountries);
        return "customer/account_form";
    }



    @PostMapping("/update_account_details")
    public String updateAccountDetails(Customer customer, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        customerService.update(customer);
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");
        updateNameForAuthenticatedCustomer(httpServletRequest, customer);
        return "redirect:/account_details";
    }

    private void updateNameForAuthenticatedCustomer(HttpServletRequest request, Customer customer) {
        Object principal = request.getUserPrincipal();
        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails customerUserDetails = getCustomerUserDetailsObject(principal);
            Customer authenticatedCustomer = customerUserDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User auth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            auth2User.setFullName(fullName);
        }

    }

    private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
        CustomerUserDetails userDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();

        } else if (principal instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        }

        return userDetails;
    }

    @GetMapping("/reset_password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        Customer customer = customerService.getByResetPasswordToken(token);
        if (customer != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", "invalid token");
            return "message";
        }
        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        try {
            customerService.updatePassword(token, password);
            model.addAttribute("pageTitle", "Reset Your Password");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "You have successfully changed your password");
            return "message";
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", e.getMessage());
            return "message";
        }
    }
}
