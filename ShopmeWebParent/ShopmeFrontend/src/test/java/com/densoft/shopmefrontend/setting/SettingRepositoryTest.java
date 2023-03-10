package com.densoft.shopmefrontend.setting;

import com.densoft.shopmecommon.entity.setting.SettingCategory;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class SettingRepositoryTest {

    @Autowired
    private SettingRepository settingRepository;


    @Test
    public void testFindByTwoCategories() {
        List<Setting> settings = settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        settings.forEach(System.out::println);
    }

}