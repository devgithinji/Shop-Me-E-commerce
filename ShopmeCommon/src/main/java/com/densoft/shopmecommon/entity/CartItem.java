package com.densoft.shopmecommon.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;


    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", customer=" + customer.getFullName() +
                ", product=" + product.getName() +
                ", quantity=" + quantity +
                '}';
    }
}