package com.densoft.shopmefrontend.checkout.paypal;

import com.densoft.shopmefrontend.setting.PaymentSettingBag;
import com.densoft.shopmefrontend.setting.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Component
public class PayPalService {

    public static final String GET_ORDER_API = "/v2/checkout/orders/";

    @Autowired
    private SettingsService settingsService;

    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);

        return orderResponse.validate(orderId);
    }

    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);
        HttpStatus statusCode = response.getStatusCode();

        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionForNoOkResponse(statusCode);
        }

        return response.getBody();
    }

    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettingBag = settingsService.getPaymentSettings();
        String baseUrl = paymentSettingBag.getURL();

        String requestUrl = baseUrl + GET_ORDER_API + orderId;
        String clientSecret = paymentSettingBag.getClientSecret();
        String clientId = paymentSettingBag.getClientID();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.add("Accept-Language", "en_us");
        httpHeaders.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
    }

    private void throwExceptionForNoOkResponse(HttpStatus statusCode) throws PayPalApiException {
        String message = switch (statusCode) {
            case NOT_FOUND -> "Order ID not found";
            case BAD_REQUEST -> "Bad request to PayPal Checkout API";
            case INTERNAL_SERVER_ERROR -> "PayPal server error";
            default -> "PayPal returned non-ok status code";
        };

        throw new PayPalApiException(message);
    }

}
