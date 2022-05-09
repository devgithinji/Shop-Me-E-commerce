package com.densoft.shopmeAdmin.report;

import com.densoft.shopmeAdmin.setting.SettingsService;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private SettingsService settingService;


    @GetMapping("/reports")
    public String viewSalesReportHome(HttpServletRequest request) {
        loadCurrencySetting(request);
        return "reports/reports";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
}
