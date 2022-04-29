package com.densoft.shopmefrontend.setting;

import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Integer> {
    List<State> findByCountryOrderByNameAsc(Country country);
}
