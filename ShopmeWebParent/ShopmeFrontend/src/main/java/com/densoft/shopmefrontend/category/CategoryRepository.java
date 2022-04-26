package com.densoft.shopmefrontend.category;

import com.densoft.shopmecommon.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category  c WHERE c.enabled = true ORDER BY c.name ASC")
    List<Category> findAllEnabled();

    Category findByAliasAndEnabledTrue(String alias);
}
