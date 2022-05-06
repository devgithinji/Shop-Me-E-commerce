package com.densoft.shopmefrontend.paypal;

import com.densoft.shopmefrontend.checkout.paypal.PayPalOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


public class PayPalApiTests {
    public static final String BASE_URL = "https://api.sandbox.paypal.com";
    public static final String GET_ORDER_API = "/v2/checkout/orders/";
    public static final String CLIENT_ID = "ARxzLOrd5DW8sGLlD4_XwiMhCZaQsOeptUneVR1nViwKkB4xi7Ss-BRCboVs865re0Twi3KcYW647V0w";
    public static final String CLIENT_SECRET = "EB1FlaXL4yvKVM0CmVFLOm5q29425nOJxYjGO5nAxcfonHnyxMBAlIMlzKtZYRKz_9vC7zVs6ulnUr6x";

    @Test
    public void testGetOrderDetails() {
        String orderId = "6SM28250GV5786703";
        String requestUrl = BASE_URL + GET_ORDER_API + orderId;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Accept-Language", "en_us");
        httpHeaders.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

        System.out.println("Order Id: " + orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));
    }
}
