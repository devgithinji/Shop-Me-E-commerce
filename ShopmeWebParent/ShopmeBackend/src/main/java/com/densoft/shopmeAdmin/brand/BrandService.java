package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmecommon.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listBrands() {
        return brandRepository.findAll();
    }


    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isPresent()) {
            return optionalBrand.get();
        }
        throw new BrandNotFoundException("Brand with ID: " + id + " not found");
    }


    public void delete(Integer id) throws BrandNotFoundException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isPresent()) {
            brandRepository.delete(optionalBrand.get());
        } else {
            throw new BrandNotFoundException("Brand with ID: " + id + " not found");
        }

    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = brandRepository.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null) return "Duplicate";
        } else {
            if (brandByName != null && brandByName.getId() != id) {
                return "Duplicate";
            }
        }

        return "OK";

    }
}
