package com.densoft.shopmecommon.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class State extends IdBasedEntity {


    @Column(nullable = false, length = 45)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @ToString.Exclude
    private Country country;

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }
}
