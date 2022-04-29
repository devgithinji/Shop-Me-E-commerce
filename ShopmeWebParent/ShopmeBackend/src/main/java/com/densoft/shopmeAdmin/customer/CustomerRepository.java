package com.densoft.shopmeAdmin.customer;

import com.densoft.shopmecommon.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE c.firstName LIKE %?1% "
            + "OR c.lastName LIKE %?1% "
            + "OR c.email LIKE %?1% "
            + "OR c.addressLine1 LIKE %?1% "
            + "OR c.addressLine2 LIKE %?1% "
            + "OR c.city LIKE %?1% "
            + "OR c.state LIKE %?1%"
            + "OR c.country.name LIKE %?1%"
            + "OR c.postalCode LIKE %?1%"
    )
    Page<Customer> findAll(String keyWord, Pageable pageable);


    Customer findByEmail(String email);
}
