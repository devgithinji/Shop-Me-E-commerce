package com.densoft.shopmeAdmin.customer;

import com.densoft.shopmeAdmin.paging.PagingAndSortingHelper;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    public static final int CUSTOMERS_PER_PAGE = 10;


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Customer> listAll() {
        return customerRepository.findAll();
    }


    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepository);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) throws CustomerNotFoundException {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isEmpty()) throw new CustomerNotFoundException("Customer with ID: " + id + " not found");
        Customer customer = customerOptional.get();
        customer.setEnabled(enabled);
        customerRepository.save(customer);
    }

    public Customer getCustomer(Integer id) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            return optionalCustomer.get();
        }
        throw new CustomerNotFoundException("Customer with ID: " + id + " not found");
    }

    public boolean isEmailUnique(Integer id, String email) {

        boolean isCreatingNew = (id == null || id == 0);
        Customer customer = customerRepository.findByEmail(email);

        if (isCreatingNew) {
            if (customer != null) {
                return false;
            }
        } else {
            if (customer.getId() != id) {
                return false;
            }
        }

        return true;

    }

    public void delete(Integer id) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        optionalCustomer.ifPresent(customer -> customerRepository.delete(customer));
        throw new CustomerNotFoundException("Customer with ID: " + id + " not found");
    }

    public void save(Customer customer) {
        Customer existingCustomer = customerRepository.findById(customer.getId()).get();

        if (customer.getPassword().isEmpty() || customer.getPassword() == null) {
            customer.setPassword(existingCustomer.getPassword());
        } else {
            encodePassword(customer);
        }

        customer.setVerificationCode(existingCustomer.getVerificationCode());
        customer.setEnabled(existingCustomer.isEnabled());
        customer.setCreatedTime(existingCustomer.getCreatedTime());

        customerRepository.save(customer);
    }


    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }
}
