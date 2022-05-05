package com.densoft.shopmefrontend.setting;

import com.densoft.shopmecommon.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}
