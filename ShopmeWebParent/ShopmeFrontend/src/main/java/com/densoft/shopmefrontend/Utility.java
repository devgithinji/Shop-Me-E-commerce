package com.densoft.shopmefrontend;

import com.densoft.shopmefrontend.security.CustomerOAuth2User;
import com.densoft.shopmefrontend.setting.CurrencySettingBag;
import com.densoft.shopmefrontend.setting.EmailSettingBag;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

public class Utility {
    public static String generateSiteUrl(HttpServletRequest httpServletRequest) {
        String siteUrl = httpServletRequest.getRequestURL().toString();
        return siteUrl.replace(httpServletRequest.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag emailSettings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailSettings.getHost());
        mailSender.setPort(emailSettings.getPort());
        mailSender.setUsername(emailSettings.getUsername());
        mailSender.setPassword(emailSettings.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", emailSettings.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", emailSettings.getSmtpSecured());
        mailSender.setJavaMailProperties(mailProperties);
        return mailSender;
    }


    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if (principal == null) {
            return null;
        }
        String customerEmail = null;
        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User auth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            customerEmail = auth2User.getEmail();
        }
        return customerEmail;
    }

    public static String formatCurrency(float amount, CurrencySettingBag currencySettings) {
        String symbol = currencySettings.getSymbol();
        String symbolPosition = currencySettings.getSymbolPosition();
        String decimalPointType = currencySettings.getDecimalPointType();
        String thousandPointType = currencySettings.getThousandPointType();
        int decimalDigits = currencySettings.getDecimalDigits();

        String pattern = symbolPosition.equals("Before Price") ? symbol : "";
        pattern += "###,###";

        if (decimalDigits > 0) {
            pattern += ".";
            for (int count = 1; count <= decimalDigits; count++) pattern += "#";
        }

        pattern += symbolPosition.equals("After Price") ? symbol : "";

        char thousandsSeparator = thousandPointType.equals("POINT") ? '.' : ',';
        char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';


        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandsSeparator);
        DecimalFormat decimalFormat = new DecimalFormat(pattern, decimalFormatSymbols);
        return decimalFormat.format(amount);
    }

}
