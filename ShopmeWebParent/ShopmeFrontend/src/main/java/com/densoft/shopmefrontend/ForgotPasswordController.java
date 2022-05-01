package com.densoft.shopmefrontend;

import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import com.densoft.shopmefrontend.customer.CustomerService;
import com.densoft.shopmefrontend.setting.EmailSettingBag;
import com.densoft.shopmefrontend.setting.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {

        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = Utility.generateSiteUrl(request) + "/reset_password?token=" + token;
            sendEmail(link, email);
            model.addAttribute("message", "We have sent a reset password link to the email");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "could not send email");
        }
        return "customer/forgot_password_form";
    }

    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingsService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        String toAddress = email;
        String subject = "Here is the link to reset your password";
        String content = "<p>Hello</p>"
                + "<p>You have requested to reset your password</p>"
                + "<p>Click the link below to change your password</p>"
                + "<p><a href=\"" + link + "\" >Change my password</a ></p > "
                + "<p>Ignore this email if you do remember your password, or you have not made the request.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);
        mailSender.send(message);

    }
}
