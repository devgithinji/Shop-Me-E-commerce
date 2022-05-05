package com.densoft.shopmefrontend.setting;

import com.densoft.shopmecommon.entity.Currency;
import com.densoft.shopmecommon.entity.setting.SettingCategory;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsService {
    private final SettingRepository settingRepository;

    private final CurrencyRepository currencyRepository;

    public SettingsService(SettingRepository settingRepository, CurrencyRepository currencyRepository) {
        this.settingRepository = settingRepository;
        this.currencyRepository = currencyRepository;
    }


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


    public PaymentSettingBag getPaymentSettings() {
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.PAYMENT);
        return new PaymentSettingBag(currencySettings);
    }

    public String getCurrencyCode() {
        Setting setting = settingRepository.findByKey("CURRENCY_ID");
        Integer currencyId = Integer.parseInt(setting.getValue());
        Currency currency = currencyRepository.findById(currencyId).get();
        return currency.getCode();
    }

}
