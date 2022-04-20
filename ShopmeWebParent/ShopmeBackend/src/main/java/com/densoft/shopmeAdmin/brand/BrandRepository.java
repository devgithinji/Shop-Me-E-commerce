package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Brand findByName(String name);
}
