package com.densoft.shopmecommon.entity.product;

import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import com.densoft.shopmecommon.entity.IdBasedEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@ToString
public class Product extends IdBasedEntity {

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> details = new ArrayList<>();

    private int reviewCount;

    private float averageRating;


    @Transient private boolean customerCanReview;

    @Transient private boolean reviewedByCustomer;

    public Product(Integer id) {
        this.id = id;
    }

    public Product(String name) {
        this.name = name;
    }

    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    public void addDetail(String name, String value) {
        this.details.add(new ProductDetail(name, value, this));
    }


    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null) return "/images/image-thumbnail.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }

    public boolean containsImageName(String fileName) {
        for (ProductImage image : images) {
            if (image.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void addDetail(Integer id, String name, String value) {
        this.details.add(new ProductDetail(id, name, value, this));
    }

    @Transient
    public String getShortName() {
        if (name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }
        return name;
    }

    @Transient
    public float getDiscountPrice() {
        if (discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }


    public boolean isCustomerCanReview() {
        return customerCanReview;
    }

    public void setCustomerCanReview(boolean customerCanReview) {
        this.customerCanReview = customerCanReview;
    }

    public boolean isReviewedByCustomer() {
        return reviewedByCustomer;
    }

    public void setReviewedByCustomer(boolean reviewedByCustomer) {
        this.reviewedByCustomer = reviewedByCustomer;
    }
}