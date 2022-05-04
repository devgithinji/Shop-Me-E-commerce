package com.densoft.shopmeAdmin.setting;

import com.densoft.shopmecommon.entity.setting.SettingCategory;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class SettingRepositoryTest {
    @Autowired
    private SettingRepository settingRepository;


    @Test
    public void testCreateGeneralSettings() {
        Setting siteName = new Setting("SITE_NAME", "ShopMe", SettingCategory.GENERAL);
        Setting siteLogo = new Setting("SITE_LOGO", "shopme.png", SettingCategory.GENERAL);
        Setting copyRight = new Setting("COPY_RIGHT", "Copyright (C) 2022 ShopMe Ltd", SettingCategory.GENERAL);
        List<Setting> savedSettings = settingRepository.saveAll(List.of(siteName, siteLogo, copyRight));
        assertThat(savedSettings.size()).isNotEqualTo(0);
    }

    @Test
    public void testCreateCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "point", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);
        settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandPointType));
    }

    @Test
    public void testListSettingsByCategory() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.GENERAL);
        settings.forEach(System.out::println);
    }


}