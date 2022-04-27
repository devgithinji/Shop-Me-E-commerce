package com.densoft.shopmecommon.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 5)
    private String code;

    @ToString.Exclude
    @OneToMany(mappedBy = "country")
    private Set<State> states;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }


}
