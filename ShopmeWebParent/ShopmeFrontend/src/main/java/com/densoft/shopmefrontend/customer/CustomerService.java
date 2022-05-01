package com.densoft.shopmefrontend.customer;

import com.densoft.shopmecommon.entity.AuthenticationType;
import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.exception.CustomerNotFoundException;
import com.densoft.shopmefrontend.setting.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setAuthenticationType(AuthenticationType.DATABASE);
        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);
        customerRepository.save(customer);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customer.setEnabled(true);
            customer.setVerificationCode(null);
            customerRepository.save(customer);
            return true;
        }
    }

    public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
        Customer existingCustomer = customerRepository.findById(customer.getId()).get();
        if (!existingCustomer.getAuthenticationType().equals(authenticationType)) {
            existingCustomer.setAuthenticationType(authenticationType);
            customerRepository.save(existingCustomer);
        }

    }

    public void addNewCustomerUponAuthLogin(String name, String email, String code, AuthenticationType authenticationType) {
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name, customer);
        customer.setEnabled(true);
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setAddressLine2("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepository.findByCode(code));
        customerRepository.save(customer);
    }

    private void setName(String name, Customer customer) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);
            String lastName = name.replace(firstName, "").trim();
            customer.setLastName(lastName);
        }
    }

    public void update(Customer customer) {
        Customer existingCustomer = customerRepository.findById(customer.getId()).get();

        if (existingCustomer.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
            if (customer.getPassword().isEmpty() || customer.getPassword() == null) {
                customer.setPassword(existingCustomer.getPassword());
            } else {
                encodePassword(customer);
            }
        } else {
            customer.setPassword(existingCustomer.getPassword());
        }


        customer.setVerificationCode(existingCustomer.getVerificationCode());
        customer.setEnabled(existingCustomer.isEnabled());
        customer.setCreatedTime(existingCustomer.getCreatedTime());
        customer.setAuthenticationType(existingCustomer.getAuthenticationType());
        customer.setResetPasswordToken(existingCustomer.getResetPasswordToken());

        customerRepository.save(customer);
    }

    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
            return token;
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email: " + email);
        }
    }

    public Customer getByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByResetPasswordToken(token);
        if (customer == null) {
            throw new CustomerNotFoundException("Invalid token");
        }
        customer.setPassword(newPassword);
        customer.setResetPasswordToken(null);
        encodePassword(customer);
        customerRepository.save(customer);
    }
}
