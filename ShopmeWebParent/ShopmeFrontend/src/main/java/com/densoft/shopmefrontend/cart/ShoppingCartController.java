package com.densoft.shopmefrontend.cart;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.CartItem;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.ShippingRate;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import com.densoft.shopmefrontend.Utility;
import com.densoft.shopmefrontend.address.AddressService;
import com.densoft.shopmefrontend.customer.CustomerService;
import com.densoft.shopmefrontend.shipping.ShippingRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    private final CustomerService customerService;

    private final AddressService addressService;
    private final ShippingRateService shippingRateService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, CustomerService customerService, AddressService addressService, ShippingRateService shippingRateService) {
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItemList = shoppingCartService.listCartItems(customer);

        float estimatedTotal = 0.0F;

        for (CartItem cartItem : cartItemList) {
            estimatedTotal += cartItem.getSubTotal();
        }

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.shippingRateForAddress(defaultAddress);
        } else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.shippingRateForCustomer(customer);
        }

        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("cartItems", cartItemList);
        model.addAttribute("estimatedTotal", estimatedTotal);
        return "cart/shopping_cart";
    }


    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
