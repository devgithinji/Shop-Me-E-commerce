package com.densoft.shopmefrontend.customer;

import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmefrontend.Utility;
import com.densoft.shopmefrontend.setting.EmailSettingBag;
import com.densoft.shopmefrontend.setting.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
}
