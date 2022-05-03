package com.densoft.shopmeAdmin.order;

import com.densoft.shopmeAdmin.paging.PagingAndSortingHelper;
import com.densoft.shopmeAdmin.paging.PagingAndSortingParam;
import com.densoft.shopmeAdmin.setting.SettingsService;
import com.densoft.shopmecommon.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {
    private final String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingsService settingService;

    @GetMapping("/orders")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum,
            HttpServletRequest request) {

        orderService.listByPage(pageNum, helper);
        loadCurrencySetting(request);

        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();

        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
}
