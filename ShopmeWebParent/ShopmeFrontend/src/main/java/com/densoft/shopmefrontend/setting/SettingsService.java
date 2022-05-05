package com.densoft.shopmefrontend.setting;

import com.densoft.shopmecommon.entity.setting.SettingCategory;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {
    @Autowired
    private SettingRepository settingRepository;


    public List<Setting> getGeneralSetting() {

        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

    }

    public EmailSettingBag getEmailSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));
        return new EmailSettingBag(settings);

    }

    public CurrencySettingBag getCurrencySettings() {
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        return new CurrencySettingBag(currencySettings);
    }

}
