package com.densoft.shopmefrontend.cart;

import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import com.densoft.shopmefrontend.Utility;
import com.densoft.shopmefrontend.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId, @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = shoppingCartService.addProduct(productId, quantity, customer);
            return updatedQuantity + " item(s) of this product were added to your shopping cart.";
        } catch (CustomerNotFoundException e) {
            return "You must login to add this product to cart";
        } catch (ShoppingCartException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable(name = "productId") Integer productId, @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subTotal = shoppingCartService.updateQuantity(productId, quantity, customer);
            return String.valueOf(subTotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of  product";
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }
        return customerService.getCustomerByEmail(email);
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Integer productId,
                                HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            shoppingCartService.removeProduct(productId, customer);

            return "The product has been removed from your shopping cart.";

        } catch (CustomerNotFoundException e) {
            return "You must login to remove product.";
        }
    }

}
