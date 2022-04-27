package com.densoft.shopmeAdmin.setting.country;

import com.densoft.shopmecommon.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateCountry() {
        Country india = new Country("China", "CN");
        Country kenya = new Country("Kenya", "KE");
        Country uganda = new Country("Uganda", "UG");
        Country japan = new Country("Japan", "JP");
        Country kuwait = new Country("Kuwait", "KW");
        Country malawi = new Country("Malawi", "MW");
        List<Country> countryList = countryRepository.saveAll(List.of(india, kenya, uganda, japan, kuwait, malawi));
        assertThat(countryList).isNotNull();
        assertThat(countryList.size()).isGreaterThan(0);
    }

    @Test
    public void testListCountries() {
        List<Country> countryList = countryRepository.findAllByOrderByNameAsc();
        countryList.forEach(System.out::println);
        assertThat(countryList.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry() {
        Integer id = 1;
        String name = "Republic of India";
        Country country = countryRepository.findById(id).get();
        country.setName(name);
        Country updatedCountry = countryRepository.save(country);
        assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCountry() {
        Integer id = 3;
        Country country = countryRepository.findById(id).get();
        assertThat(country).isNotNull();
    }

    @Test
    public void testDeleteCountry() {
        Integer id = 5;
        countryRepository.deleteById(id);
        Optional<Country> optionalCountry = countryRepository.findById(id);
        assertThat(optionalCountry.isEmpty());
    }

}