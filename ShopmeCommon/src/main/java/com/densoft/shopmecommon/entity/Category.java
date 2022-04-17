package com.densoft.shopmecommon.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;


    public Category(Integer id) {
        this.id = id;
    }

    public static Category copyIdAndName(Category category) {
        Category newCategory = new Category();
        newCategory.setId(category.getId());
        newCategory.setName(category.getName());
        return newCategory;
    }

    public static Category copyIdAndName(Integer id, String name) {
        Category newCategory = new Category();
        newCategory.setId(id);
        newCategory.setName(name);
        return newCategory;
    }

    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.png";
    }

    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private Set<Category> children = new HashSet<>();
}
