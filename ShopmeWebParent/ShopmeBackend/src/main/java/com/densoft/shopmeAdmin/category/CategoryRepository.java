package com.densoft.shopmeAdmin.category;

import com.densoft.shopmecommon.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
