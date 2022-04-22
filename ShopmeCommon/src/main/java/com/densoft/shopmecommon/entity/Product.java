package com.densoft.shopmecommon.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, length = 256, nullable = false)
    private String name;
    @Column(unique = true, length = 256, nullable = false)
    private String alias;
    @Column(length = 512, name = "short_description", nullable = false)
    private String shortDescription;
    @Column(length = 4096, name = "full_description", nullable = false)
    private String fullDescription;

    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private Date createdTime;
    @UpdateTimestamp
    @Column(name = "updated_time")
    private Date updatedTime;

    @ColumnDefault("true")
    private boolean enabled;
    @Column(name = "in_stock")
    @ColumnDefault("true")
    private boolean inStock;

    @ColumnDefault("0")
    private float cost;
    @ColumnDefault("0")
    private float price;
    @ColumnDefault("0")
    @Column(name = "discount_percent")
    private float discountPercent;


    @ColumnDefault("0")
    private float length;
    @ColumnDefault("0")
    private float width;
    @ColumnDefault("0")
    private float height;
    @ColumnDefault("0")
    private float weight;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductImage> images = new HashSet<>();

    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }


    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null) return "/images/image-thumbnail.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }
}