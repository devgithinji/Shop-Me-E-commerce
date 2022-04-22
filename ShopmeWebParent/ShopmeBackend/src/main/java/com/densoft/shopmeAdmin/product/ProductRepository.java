package com.densoft.shopmeAdmin.product;

import com.densoft.shopmecommon.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);
}
