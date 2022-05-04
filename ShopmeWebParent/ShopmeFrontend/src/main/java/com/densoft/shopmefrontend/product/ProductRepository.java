package com.densoft.shopmefrontend.product;

import com.densoft.shopmecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true "
            + "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
            + "ORDER BY p.name ASC"
    )
    Page<Product> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);


    Product findByAlias(String alias);

    @Query(value = "SELECT * FROM products WHERE enabled = true AND "
            + "MATCH(name, short_description, full_description) AGAINST (?1)", nativeQuery = true)
    Page<Product> Search(String keyword, Pageable pageable);
}
