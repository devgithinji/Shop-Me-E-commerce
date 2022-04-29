package com.densoft.shopmeAdmin.customer;

import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    public static final int CUSTOMERS_PER_PAGE = 10;


    @Autowired
    private CustomerRepository customerRepository;


    public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyWord) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMERS_PER_PAGE, sort);
        if (keyWord != null) {
            return customerRepository.findAll(keyWord, pageable);
        }
        return customerRepository.findAll(pageable);
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
        if (optionalCustomer.isPresent()) {
            customerRepository.delete(optionalCustomer.get());
        }
        throw new CustomerNotFoundException("Customer with ID: " + id + " not found");
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }
}
