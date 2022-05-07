package com.densoft.shopmeAdmin.setting;

import com.densoft.shopmeAdmin.currency.CurrencyRepository;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.Constants;
import com.densoft.shopmecommon.entity.Currency;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class SettingController {
    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Setting> settingList = settingsService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();
        model.addAttribute("currencies", listCurrencies);
        for (Setting setting : settingList) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);

        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSetting(
            @RequestParam("fileImage") MultipartFile multipartFile,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws IOException {

        GeneralSettingBag generalSettingBag = settingsService.getGeneralSetting();

        saveSiteLogo(multipartFile, generalSettingBag);
        saveCurrencySymbol(request, generalSettingBag);
        updateSettingValuesFromForm(request, generalSettingBag.list());

        redirectAttributes.addFlashAttribute("message", "General settings has been saved.");

        return "redirect:/settings";
    }

    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag generalSettingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            generalSettingBag.updateSiteLogo(value);

            String uploadDir = "../site-logo/";
            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> optionalCurrency = currencyRepository.findById(currencyId);
        if (optionalCurrency.isPresent()) {
            Currency currency = optionalCurrency.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> settingList) {
        for (Setting setting : settingList) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }
        settingsService.saveAll(settingList);
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> mailMailServerSettings = settingsService.getMailServerSettings();
        updateSettingValuesFromForm(request, mailMailServerSettings);
        redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved");
        return "redirect:/settings#mailServer";
    }


    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> mailMailTemplateSettings = settingsService.getMailTemplateSettings();
        updateSettingValuesFromForm(request, mailMailTemplateSettings);
        redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved");
        return "redirect:/settings#mailTemplates";
    }


    @PostMapping("/settings/save_payment")
    public String savePaymentSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> paymentSettings = settingsService.getPaymentSettings();
        updateSettingValuesFromForm(request, paymentSettings);
        redirectAttributes.addFlashAttribute("message", "Payment settings have been saved");
        return "redirect:/settings#payment";
    }


}
