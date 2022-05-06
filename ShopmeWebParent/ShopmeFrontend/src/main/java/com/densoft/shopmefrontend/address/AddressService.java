package com.densoft.shopmefrontend.address;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public void save(Address address) {
        addressRepository.save(address);
    }

    public Address get(Integer id) {
        return addressRepository.findById(id).get();
    }

    public void delete(Integer id) {
        addressRepository.deleteById(id);
    }

    public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
        if (defaultAddressId > 0) {
            Address address = addressRepository.findById(defaultAddressId).get();
            address.setDefaultForShipping(true);
            addressRepository.save(address);
        }

        List<Address> addressList = addressRepository.findByCustomer(new Customer(customerId));
        addressList.forEach(address -> {
            if (!address.getId().equals(defaultAddressId)) {
                address.setDefaultForShipping(false);
                addressRepository.save(address);
            }
        });

    }

    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findByCustomerAndDefaultForShipping(customer, true);
    }

}
