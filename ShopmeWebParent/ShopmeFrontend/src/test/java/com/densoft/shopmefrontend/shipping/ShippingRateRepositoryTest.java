package com.densoft.shopmefrontend.shipping;

import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class ShippingRateRepositoryTest {

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Test
    public void testFindByCountryAndState() {
        Country usa = new Country(234);
        String state = "New York";
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(usa, state);
        System.out.println(shippingRate);
    }

}