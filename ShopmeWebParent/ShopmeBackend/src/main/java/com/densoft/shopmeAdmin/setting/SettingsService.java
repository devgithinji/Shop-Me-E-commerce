package com.densoft.shopmeAdmin.setting;

import com.densoft.shopmecommon.entity.setting.SettingCategory;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsService {
    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> listAllSettings() {
        return settingRepository.findAll();
    }

    public GeneralSettingBag getGeneralSetting() {
        List<Setting> settings = new ArrayList<>();

        List<Setting> generalSetting = settingRepository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);


        settings.addAll(generalSetting);
        settings.addAll(currencySettings);

        return new GeneralSettingBag(settings);

    }

    public void saveAll(List<Setting> settings) {
        settingRepository.saveAll(settings);
    }

    public List<Setting> getMailServerSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
    }

    public List<Setting> getMailTemplateSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
    }

    public List<Setting> getCurrencySettings() {
        return settingRepository.findByCategory(SettingCategory.CURRENCY);
    }
}
