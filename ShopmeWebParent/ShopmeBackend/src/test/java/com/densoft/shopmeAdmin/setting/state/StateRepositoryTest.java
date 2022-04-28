package com.densoft.shopmeAdmin.setting.state;

import com.densoft.shopmeAdmin.setting.country.CountryRepository;
import com.densoft.shopmecommon.entity.Country;
import com.densoft.shopmecommon.entity.State;
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
class StateRepositoryTest {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateStatesInIndia() {
        Integer countryId = 1;
        Country country = countryRepository.findById(countryId).get();

        List<State> states = stateRepository.saveAll(List.of(
                new State("karnataka", country),
                new State("pujab", country),
                new State("Uttar Pradesh", country),
                new State("West Bengal", country)
        ));

        assertThat(states).isNotEmpty();
    }

    @Test
    public void testCreateStatesInKenya() {
        Integer countryId = 2;
        Country country = countryRepository.findById(countryId).get();

        List<State> states = stateRepository.saveAll(List.of(
                new State("kiambu", country),
                new State("Nairobi", country),
                new State("Nakuru", country),
                new State("Nyeri", country)
        ));

        assertThat(states).isNotEmpty();
    }

    @Test
    public void testListStatesByCountry() {
        Integer countryId = 2;
        Country country = countryRepository.findById(countryId).get();
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(country);
        listStates.forEach(System.out::println);
        assertThat(listStates.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateState() {
        Integer stateId = 5;
        String stateName = "Kiambu updated";
        State state = stateRepository.findById(stateId).get();
        state.setName(stateName);
        State updatedState = stateRepository.save(state);

        assertThat(updatedState.getName()).isEqualTo(stateName);
    }

    @Test
    public void testGetState() {
        Integer stateId = 1;
        Optional<State> findById = stateRepository.findById(stateId);
        assertThat(findById.isPresent());
    }

    @Test
    public void testDeleteState() {
        Integer stateId = 8;
        stateRepository.deleteById(stateId);
        Optional<State> optionalState = stateRepository.findById(stateId);
        assertThat(optionalState.isEmpty());

    }

}