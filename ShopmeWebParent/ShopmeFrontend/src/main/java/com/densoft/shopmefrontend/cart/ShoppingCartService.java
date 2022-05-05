package com.densoft.shopmefrontend.cart;

import com.densoft.shopmecommon.entity.CartItem;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.product.Product;
import com.densoft.shopmefrontend.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
            if (updatedQuantity > 5) {
                throw new ShoppingCartException("Could not add " + quantity +
                        " more item(s), maximum items allowed is 5 and you already have "
                        + cartItem.getQuantity() + " items in cart");
            }
        } else {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }
        cartItem.setQuantity(updatedQuantity);

        cartItemRepository.save(cartItem);

        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepository.findById(productId).get();
        float subtotal = product.getDiscountPrice() * quantity;
        return subtotal;
    }

    public void removeProduct(Integer productId, Customer customer) {
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer) {
        cartItemRepository.deleteByCustomer(customer.getId());
    }
}
