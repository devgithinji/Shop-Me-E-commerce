package com.densoft.shopmefrontend.customer;

import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCreateCustomerOne() {
        Integer countryId = 234;
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("David");
        customer.setLastName("Fontaine");
        customer.setPassword("password");
        customer.setEmail("david@gmail.com");
        customer.setPhoneNumber("0799566927");
        customer.setAddressLine1("1927 West Drive");
        customer.setCity("Sacramento");
        customer.setState("California");
        customer.setPostalCode("97745");

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateCustomerTwo() {
        Integer countryId = 106;
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Test");
        customer.setLastName("user");
        customer.setPassword("password");
        customer.setEmail("testuser@gmail.com");
        customer.setPhoneNumber("0799566927");
        customer.setAddressLine1("1927 Westlands");
        customer.setCity("Mumbai");
        customer.setState("Maharashtra");
        customer.setPostalCode("97746");

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);

    }

    @Test
    public void testListCustomers() {
        List<Customer> customerList = customerRepository.findAll();
        customerList.forEach(System.out::println);
        assertThat(customerList).hasSizeGreaterThan(1);
    }

    @Test
    public void testUpdateCustomer() {
        Integer customerId = 1;
        String lastName = "Stanfield";
        Customer customer = customerRepository.findById(customerId).get();
        customer.setLastName(lastName);
        customer.setEnabled(true);

        Customer updatedCustomer = customerRepository.save(customer);
        assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void testGetCustomer() {
        Integer customerId = 2;
        Optional<Customer> findById = customerRepository.findById(customerId);
        assertThat(findById).isPresent();
        Customer customer = findById.get();
        System.out.println(customer);
    }

    @Test
    public void deleteCustomer() {
        Integer customerId = 2;
        customerRepository.deleteById(customerId);
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    public void testFindByEmail() {
        String email = "david@gmail.com";
        Customer customer = customerRepository.findByEmail(email);
        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    public void testFindByVerificationCode() {
        String code = "code_123";
        Customer customer = customerRepository.findByVerificationCode(code);
        assertThat(customer).isNull();
    }

    @Test
    public void testEnableCustomer() {
        Integer customerId = 1;
        Customer customer = customerRepository.findById(customerId).get();
        customer.setEnabled(true);
        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer.isEnabled()).isTrue();
    }


}