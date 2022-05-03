package com.densoft.shopmefrontend.address;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByCustomer(Customer customer);


    Address findByCustomerAndDefaultForShipping(Customer customer, boolean defaultForShipping);

}
