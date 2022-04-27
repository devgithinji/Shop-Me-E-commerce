package com.densoft.shopmeAdmin.setting.country;

import com.densoft.shopmecommon.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();
}
