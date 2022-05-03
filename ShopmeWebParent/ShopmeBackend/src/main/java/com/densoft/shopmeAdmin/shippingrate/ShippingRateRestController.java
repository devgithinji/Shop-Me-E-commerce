package com.densoft.shopmeAdmin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

public class ShippingRateRestController {
    @Autowired
    private ShippingRateService service;

    @PostMapping("/get_shipping_cost")
    public String getShippingCost(Integer productId, Integer countryId, String state)
            throws ShippingRateNotFoundException {
        float shippingCost = service.calculateShippingCost(productId, countryId, state);
        return String.valueOf(shippingCost);
    }
}
