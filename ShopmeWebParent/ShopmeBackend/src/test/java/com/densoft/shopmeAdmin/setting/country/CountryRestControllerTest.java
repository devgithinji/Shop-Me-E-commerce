package com.densoft.shopmeAdmin.setting.country;


import com.densoft.shopmecommon.entity.Country;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CountryRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "password", roles = "Admin")
    public void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "password", roles = "Admin")
    public void testCreateCountry() throws Exception {
        String url = "/countries/save";
        String countryName = "Germany";
        Country country = new Country(countryName, countryName.substring(0, 2).toUpperCase());
        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Integer countryId = Integer.parseInt(response);
        assertThat(countryRepository.findById(countryId).get().getName()).isEqualTo(countryName);
    }


    @Test
    @WithMockUser(username = "nam@codejava.net", password = "password", roles = "Admin")
    public void testUpdateCountry() throws Exception {
        String url = "/countries/save";
        Integer countryId = 7;
        String countryName = "China";
        Country country = new Country(countryId, countryName, countryName.substring(0, 2).toUpperCase());
        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(String.valueOf(countryId)));
        assertThat(countryRepository.findById(countryId).get().getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "password", roles = "Admin")
    public void testDeleteCountry() throws Exception {
        Integer countryId = 7;
        String url = "/countries/delete/" + countryId;
        mockMvc.perform(get(url)).andExpect(status().isOk());
        assertThat(countryRepository.findById(countryId)).isNotPresent();
    }

}