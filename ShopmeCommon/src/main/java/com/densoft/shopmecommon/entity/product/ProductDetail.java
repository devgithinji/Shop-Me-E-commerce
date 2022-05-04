package com.densoft.shopmecommon.entity.product;

import com.densoft.shopmecommon.entity.IdBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product_details")
@Getter
@Setter
@NoArgsConstructor
public class ProductDetail extends IdBasedEntity {

    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false, length = 255)
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetail(Integer id, String name, String value, Product product) {
        super();
        this.id = id;
        this.name = name;
        this.value = value;
        this.product = product;
    }



    public ProductDetail(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
