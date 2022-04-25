package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.brand.BrandRepository;
import com.densoft.shopmeAdmin.category.CategoryRepository;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateProduct() {
        Brand brand = brandRepository.findById(37).get();
        Category category = categoryRepository.findById(5).get();

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("A good smartphone from Acer Aspire Desktop");
        product.setFullDescription("Full description for Acer Aspire");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(678);
        product.setCost(600);
        product.setEnabled(true);
        product.setInStock(true);
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(System.out::println);

        assertThat(products).isNotNull();

    }

    @Test
    public void testGetProduct() {
        Integer id = 2;
        Product product = productRepository.findById(id).get();
        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 1;
        Product product = productRepository.findById(id).get();
        product.setPrice(499);
        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getPrice()).isEqualTo(499);

    }

    @Test
    public void testDeleteProduct() {
        Integer id = 3;
        productRepository.deleteById(id);
        assertThat(productRepository.findById(3).isEmpty()).isTrue();
    }

    @Test
    public void testSaveProductWithImages() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();
        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.png");
        product.addExtraImage("extra image 2.png");
        product.addExtraImage("extra image 3.png");

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getImages().size()).isEqualTo(3);

    }


    @Test
    public void testSaveProductWithDetails() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();
        product.addDetail("Device Memory", "128 GB");
        product.addDetail("CPU Model", "MediaTek");
        product.addDetail("OS", "Android 10");
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getDetails()).isNotEmpty();
    }

}