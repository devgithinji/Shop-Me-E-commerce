package com.densoft.shopmefrontend.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("wakahiad@gmail.com")
    public void testSendOrderReturnRequestFailed() throws Exception {
        Integer orderId = 1111;
        OrderReturnRequest requestRequest = new OrderReturnRequest(orderId, "", "");

        String requestURL = "/orders/return";

        mockMvc.perform(post(requestURL).with(csrf()).contentType("application/json").content(objectMapper.writeValueAsString(requestRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @WithUserDetails("wakahiad@gmail.com")
    public void testSendOrderReturnRequestSuccessful() throws Exception {
        Integer orderId = 15;
        String reason = "I bought the wrong items";
        String note = "Please return my money";

        OrderReturnRequest requestRequest = new OrderReturnRequest(orderId, reason, note);

        String requestURL = "/orders/return";

        mockMvc.perform(post(requestURL).with(csrf()).contentType("application/json").content(objectMapper.writeValueAsString(requestRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }


}