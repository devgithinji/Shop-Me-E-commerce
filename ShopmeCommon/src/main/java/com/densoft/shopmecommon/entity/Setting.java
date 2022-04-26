package com.densoft.shopmecommon.entity;

import com.densoft.shopmecommon.SettingCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
    @Id
    @Column(name = "`key`", nullable = false, length = 128)
    private String key;
    @Column(nullable = false, length = 1024)
    private String value;
    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private SettingCategory category;

}
