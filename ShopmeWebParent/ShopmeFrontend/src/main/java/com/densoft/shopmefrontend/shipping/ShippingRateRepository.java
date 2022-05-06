package com.densoft.shopmefrontend.shipping;

import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    ShippingRate findByCountryAndState(Country country, String state);
}
