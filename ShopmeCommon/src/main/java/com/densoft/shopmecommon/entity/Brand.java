package com.densoft.shopmecommon.entity;

import com.densoft.shopmecommon.Constants;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Brand extends IdBasedEntity {


    @Column(nullable = false, length = 45, unique = true)
    private String name;
    @Column(nullable = false, length = 128)
    private String logo;

    @ManyToMany
    @JoinTable(
            name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();


    public Brand(Integer id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }


    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    @Transient
    public String getLogoPath() {
        if (this.id == null) return "/images/image-thumbnail.png";
        return Constants.S3_BASE_URI + "/brand-logos/" + this.id + "/" + this.logo;
    }

    @Override
    public String toString() {
        return name;
    }
}
