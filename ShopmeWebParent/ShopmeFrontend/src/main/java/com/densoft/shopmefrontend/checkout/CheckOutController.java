package com.densoft.shopmefrontend.checkout;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.CartItem;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.ShippingRate;
import com.densoft.shopmecommon.entity.order.Order;
import com.densoft.shopmecommon.entity.order.PaymentMethod;
import com.densoft.shopmefrontend.Utility;
import com.densoft.shopmefrontend.address.AddressService;
import com.densoft.shopmefrontend.cart.ShoppingCartService;
import com.densoft.shopmefrontend.customer.CustomerService;
import com.densoft.shopmefrontend.order.OrderService;
import com.densoft.shopmefrontend.setting.CurrencySettingBag;
import com.densoft.shopmefrontend.setting.EmailSettingBag;
import com.densoft.shopmefrontend.setting.SettingsService;
import com.densoft.shopmefrontend.shipping.ShippingRateService;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.CDATASection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckOutController {

    private final CheckOutService checkOutService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final ShoppingCartService shoppingCartService;
    private final OrderService orderService;

    private final SettingsService settingsService;

    public CheckOutController(CheckOutService checkOutService, CustomerService customerService, AddressService addressService, ShippingRateService shippingRateService, ShoppingCartService shoppingCartService, OrderService orderService, SettingsService settingsService) {
        this.checkOutService = checkOutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.settingsService = settingsService;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.shippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", customer.toString());
            shippingRate = shippingRateService.shippingRateForCustomer(customer);
        }

        if (shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItemList = shoppingCartService.listCartItems(customer);
        CheckOutInfo checkOutInfo = checkOutService.prepareCheckout(cartItemList, shippingRate);
        model.addAttribute("checkoutInfo", checkOutInfo);
        model.addAttribute("cartItems", cartItemList);


        return "checkout/checkout";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.shippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.shippingRateForCustomer(customer);
        }


        List<CartItem> cartItemList = shoppingCartService.listCartItems(customer);
        CheckOutInfo checkOutInfo = checkOutService.prepareCheckout(cartItemList, shippingRate);

        Order order = orderService.createOrder(customer, defaultAddress, cartItemList, paymentMethod, checkOutInfo);
        shoppingCartService.deleteByCustomer(customer);
        sendOrderConfirmationEmail(request, order);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingsService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String emailContent = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        String orderTime = dateFormat.format(order.getOrderTime());

        CurrencySettingBag currencySetting = settingsService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(order.getTotal(), currencySetting);

        emailContent = emailContent.replace("[[name]]", order.getCustomer().getFullName());
        emailContent = emailContent.replace("[[orderId]]", String.valueOf(order.getId()));
        emailContent = emailContent.replace("[[orderTime]]", orderTime);
        emailContent = emailContent.replace("[[shippingAddress]]", order.getShippingAddress());
        emailContent = emailContent.replace("[[total]]", totalAmount);
        emailContent = emailContent.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(emailContent, true);
        mailSender.send(message);

    }


}
