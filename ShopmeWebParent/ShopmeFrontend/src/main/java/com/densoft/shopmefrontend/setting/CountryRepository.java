package com.densoft.shopmefrontend.setting;

import com.densoft.shopmecommon.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();

    Country findByCode(String code);
}
