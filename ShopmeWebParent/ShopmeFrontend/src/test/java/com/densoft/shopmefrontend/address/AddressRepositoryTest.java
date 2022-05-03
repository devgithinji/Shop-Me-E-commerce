package com.densoft.shopmefrontend.address;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void testAddNew() {
        Integer customerId = 40;
        Integer countryId = 234;

        Address address = new Address();
        address.setCustomer(new Customer(customerId));
        address.setCountry(new Country(countryId));
        address.setFirstName("Dennis");
        address.setLastName("Wakahia");
        address.setPhoneNumber("0729149333");
        address.setAddressLine1("06 Limuru Kiambu");
        address.setCity("Nairobi");
        address.setState("Kiambu");
        address.setPostalCode("10013");

        Address savedAddresses = addressRepository.save(address);
        assertThat(savedAddresses).isNotNull();
        assertThat(savedAddresses.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 5;
        List<Address> addressList = addressRepository.findByCustomer(new Customer(customerId));
        assertThat(addressList.size()).isGreaterThan(0);
        addressList.forEach(System.out::println);
    }

    @Test
    public void testFindById() {
        Integer addressId = 1;
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        assertThat(optionalAddress).isPresent();
        System.out.println(optionalAddress.get());

    }

    @Test
    public void testUpdate() {
        Integer addressId = 1;
        String phoneNUmber = "646-232-3932";
        Address address = addressRepository.findById(addressId).get();
        address.setPhoneNumber(phoneNUmber);
        Address updatedAddress = addressRepository.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNUmber);
    }

    @Test
    public void testDelete() {
        Integer addressId = 1;
        addressRepository.deleteById(addressId);
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        assertThat(optionalAddress).isEmpty();
    }

}