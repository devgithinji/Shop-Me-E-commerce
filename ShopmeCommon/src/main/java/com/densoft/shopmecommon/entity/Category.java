package com.densoft.shopmecommon.entity;

import com.densoft.shopmecommon.Constants;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category extends IdBasedEntity {

    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIDs;


    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();

    public Category(Integer id) {
        this.id = id;
    }

    public static Category copyIdAndName(Category category) {
        Category newCategory = new Category();
        newCategory.setId(category.getId());
        newCategory.setName(category.getName());
        return newCategory;
    }

    public Category(Integer id, String name, String alias) {
        super();
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public static Category copyIdAndName(Integer id, String name) {
        Category newCategory = new Category();
        newCategory.setId(id);
        newCategory.setName(name);
        return newCategory;
    }

    public static Category copyFull(Category category) {
        Category categoryCopy = new Category();
        categoryCopy.setId(category.getId());
        categoryCopy.setName(category.getName());
        categoryCopy.setImage(category.getImage());
        categoryCopy.setAlias(category.getAlias());
        categoryCopy.setEnabled(category.isEnabled());
        categoryCopy.setHasChildren(category.getChildren().size() > 0);
        //categoryCopy.setParent(category.parent);
        return categoryCopy;
    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);
        return copyCategory;
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


    @Transient
    public String getImagePath() {
        if (this.id == null) return "/images/image-thumbnail.png";

        return Constants.S3_BASE_URI + "/category-images/" + this.id + "/" + this.image;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    @Transient
    private boolean hasChildren;

    public void setAllParentIDs(String allParentIDs) {
        this.allParentIDs = allParentIDs;
    }
}
