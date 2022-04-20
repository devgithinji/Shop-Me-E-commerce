package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmecommon.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listBrands() {
        return brandRepository.findAll();
    }


}
