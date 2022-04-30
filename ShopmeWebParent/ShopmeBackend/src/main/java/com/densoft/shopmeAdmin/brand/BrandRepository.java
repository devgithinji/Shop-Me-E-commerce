package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmeAdmin.paging.SearchRepository;
import com.densoft.shopmecommon.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends SearchRepository<Brand, Integer> {
    Brand findByName(String name);


    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(String keyWord, Pageable pageable);

    @Query("SELECT NEW Brand (b.id, b.name) FROM  Brand b ORDER BY b.name ASC")
    List<Brand> findAll();
}
