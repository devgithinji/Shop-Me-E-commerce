package com.densoft.shopmecommon.entity.product;

import com.densoft.shopmecommon.entity.IdBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
public class ProductImage extends IdBasedEntity {


    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage(Integer id, String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
    }

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }


    @Transient
    public String getImagePath() {
        return "/product-images/" + product.getId() + "/extras/" + this.name;
    }
}
