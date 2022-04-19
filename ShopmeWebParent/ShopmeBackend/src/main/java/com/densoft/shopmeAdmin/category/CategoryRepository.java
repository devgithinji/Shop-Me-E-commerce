package com.densoft.shopmeAdmin.category;

import com.densoft.shopmecommon.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {


    @Query("select c from Category c where c.parent.id is NULL")
    List<Category> findRootCategories(Sort sort);


    Category findByName(String name);


    Category findByAlias(String alias);
}
