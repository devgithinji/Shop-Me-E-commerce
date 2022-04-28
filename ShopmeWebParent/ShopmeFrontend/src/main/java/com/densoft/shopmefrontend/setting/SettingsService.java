package com.densoft.shopmefrontend.setting;

import com.densoft.shopmecommon.SettingCategory;
import com.densoft.shopmecommon.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsService {
    @Autowired
    private SettingRepository settingRepository;


    public List<Setting> getGeneralSetting() {

        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

    }

}
