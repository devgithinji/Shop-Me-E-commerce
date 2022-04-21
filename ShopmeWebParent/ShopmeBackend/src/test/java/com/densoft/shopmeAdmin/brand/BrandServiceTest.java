package com.densoft.shopmeAdmin.brand;


import com.densoft.shopmecommon.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class BrandServiceTest {
    @MockBean
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    public void testCheckUniqueInNewReturnDuplicate() {
        Integer id = null;
        String name = "Acer";
        Brand brand = new Brand(name, "default-logo.png");
        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueInNewReturnOK() {
        Integer id = null;
        String name = "Acer";

        Mockito.when(brandRepository.findByName(name)).thenReturn(null);
        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditReturnDuplicate() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(2, name, "default-logo.png");
        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("Duplicate");
    }


    @Test
    public void testCheckUniqueInEditReturnOK() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(1, name, "default-logo.png");
        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);
        String result = brandService.checkUnique(id, name);
        assertThat(result).isEqualTo("OK");
    }

}