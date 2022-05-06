package com.densoft.shopmefrontend.shipping;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {

    @Autowired
    private ShippingRateRepository shippingRateRepository;


    public ShippingRate shippingRateForCustomer(Customer customer) {
        String state = customer.getState();

        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }

        return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
    }


    public ShippingRate shippingRateForAddress(Address address) {
        String state = address.getState();

        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }

        return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
    }


}
