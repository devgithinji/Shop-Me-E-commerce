package com.densoft.shopmecommon.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

import javax.persistence.*;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
public class Currency extends IdBasedEntity {


    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 3)
    private String symbol;

    @Column(nullable = false, length = 4)
    private String code;

    public Currency(String name, String symbol, String code) {
        super();
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }

    @Override
    public String toString() {
        return name + " - " + code + " - " + symbol;
    }
}
