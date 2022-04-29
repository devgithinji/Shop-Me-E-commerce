package com.densoft.shopmefrontend.customer;

import com.densoft.shopmecommon.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByEmail(String email);

    Customer findByVerificationCode(String code);

}
