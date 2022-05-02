package com.densoft.shopmefrontend.cart;

import com.densoft.shopmecommon.entity.CartItem;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveItem() {
        Integer customerId = 1;
        Integer productId = 1;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(1);

        CartItem savedItem = cartItemRepository.save(newItem);
        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testSave2Items() {
        Integer customerId = 10;
        Integer productId = 10;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem1 = new CartItem();
        newItem1.setCustomer(customer);
        newItem1.setProduct(product);
        newItem1.setQuantity(2);

        CartItem newItem2 = new CartItem();
        newItem2.setCustomer(new Customer(customerId));
        newItem2.setProduct(new Product(8));
        newItem2.setQuantity(3);

        List<CartItem> savedItems = cartItemRepository.saveAll(List.of(newItem1, newItem2));
        assertThat(savedItems.size()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 10;
        List<CartItem> cartItems = cartItemRepository.findByCustomer(new Customer(customerId));
        cartItems.forEach(System.out::println);
        assertThat(cartItems.size()).isEqualTo(2);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Integer customerId = 1;
        Integer productId = 1;

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
        assertThat(cartItem).isNotNull();

        System.out.println(cartItem);
    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = 1;
        Integer productId = 1;
        Integer quantity = 4;

        cartItemRepository.updateQuantity(quantity, customerId, productId);
        CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item.getQuantity()).isEqualTo(4);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Integer customerId = 10;
        Integer productId = 10;
        cartItemRepository.deleteByCustomerAndProduct(customerId, productId);
        CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNull();
    }


}