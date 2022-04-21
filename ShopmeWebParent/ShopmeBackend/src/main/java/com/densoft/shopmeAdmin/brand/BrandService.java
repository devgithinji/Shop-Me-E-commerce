package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmecommon.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    public static final int BRANDS_PER_PAGE = 10;

    @Autowired
    private BrandRepository brandRepository;


    public List<Brand> listAll() {
        return brandRepository.findAll();
    }


    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyWord) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
        if (keyWord != null) {
            return brandRepository.findAll(keyWord, pageable);
        }
        return brandRepository.findAll(pageable);
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
