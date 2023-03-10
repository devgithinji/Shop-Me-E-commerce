package com.densoft.shopmeAdmin.product;

import com.densoft.shopmecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    @Query("SELECT p FROM Product  p WHERE p.name LIKE %?1% "
            + "OR p.shortDescription LIKE %?1%"
            + "OR p.fullDescription LIKE %?1%"
            + "OR p.brand.name LIKE %?1%"
            + "OR p.category.name LIKE %?1%"
    )
    Page<Product> findAll(String keyWord, Pageable pageable);

    @Query("SELECT p FROM Product  p WHERE p.category.id = ?1 "
            + " OR p.category.allParentIDs LIKE %?2%")
    Page<Product> findAllInCategory(Integer categoryId, String categoryMatchId, Pageable pageable);


    @Query("SELECT p FROM Product  p WHERE (p.category.id = ?1 "
            + " OR p.category.allParentIDs LIKE %?2%) AND"
            + "(p.name LIKE %?3% "
            + "OR p.shortDescription LIKE %?3%"
            + "OR p.fullDescription LIKE %?3%"
            + "OR p.brand.name LIKE %?3%"
            + "OR p.category.name LIKE %?3%)"
    )
    Page<Product> searchInCategory(Integer categoryId, String categoryMatchId, String keyWord, Pageable pageable);


    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    Page<Product> searchProductsByName(String keyword, Pageable pageable);
}
