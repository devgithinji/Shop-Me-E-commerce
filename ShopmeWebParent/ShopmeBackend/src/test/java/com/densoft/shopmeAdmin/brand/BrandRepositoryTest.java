package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
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
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void testCreateBrands() {
        String defaultLogo = "brand-logo.png";

        //accer
        Brand accer = new Brand("Acer", defaultLogo);
        accer.getCategories().add(new Category(6));
        //apple
        Brand apple = new Brand("Apple", defaultLogo);
        apple.getCategories().addAll(List.of(new Category(4), new Category(7)));
        //samsung
        Brand samsung = new Brand("Samsung", defaultLogo);
        samsung.getCategories().addAll(List.of(new Category(29), new Category(24)));
        //save
        brandRepository.saveAll(List.of(accer, apple, samsung));

        assertThat(brandRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    public void testListBrands() {
        List<Brand> brands = brandRepository.findAll();

        brands.forEach(brand -> {
            System.out.println(brand.getId() + " ," + brand.getName());
            brand.getCategories().forEach(category -> System.out.println(category.getName()));
        });
    }

    @Test
    public void testGetBrandById() {
        Optional<Brand> optionalBrand = brandRepository.findById(4);
        assertThat(optionalBrand.get()).isNotNull();
    }

    @Test
    public void testUpdateBrand() {
        Optional<Brand> optionalBrand = brandRepository.findById(6);
        Brand samsungBrand = optionalBrand.get();
        samsungBrand.setName("Samsung Electronics");
        Brand updatedBrand = brandRepository.save(samsungBrand);
        assertThat(updatedBrand.getName()).isEqualTo("Samsung Electronics");
    }

    @Test
    public void deleteBrand() {
        brandRepository.deleteById(5);
        assertThat(brandRepository.findById(5).isEmpty()).isTrue();
    }

}