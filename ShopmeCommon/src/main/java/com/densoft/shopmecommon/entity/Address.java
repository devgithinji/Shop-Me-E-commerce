package com.densoft.shopmecommon.entity;

import com.densoft.shopmecommon.AbstractAddressWithCountry;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address extends AbstractAddressWithCountry {


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "default_address")
    private boolean defaultForShipping;

}
