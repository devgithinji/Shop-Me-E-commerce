package com.densoft.shopmefrontend.checkout;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.CartItem;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.ShippingRate;
import com.densoft.shopmefrontend.Utility;
import com.densoft.shopmefrontend.address.AddressService;
import com.densoft.shopmefrontend.cart.ShoppingCartService;
import com.densoft.shopmefrontend.customer.CustomerService;
import com.densoft.shopmefrontend.shipping.ShippingRateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CheckOutController {

    private final CheckOutService checkOutService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final ShoppingCartService shoppingCartService;

    public CheckOutController(CheckOutService checkOutService, CustomerService customerService, AddressService addressService, ShippingRateService shippingRateService, ShoppingCartService shoppingCartService) {
        this.checkOutService = checkOutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
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


}
