package com.densoft.shopmecommon.entity;

import com.densoft.shopmecommon.AbstractAddressWithCountry;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends AbstractAddressWithCountry {


    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    private boolean enabled;

    @Column(name = "created_time")
    @CreationTimestamp
    private Date createdTime;


    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;

    @Column(name = "reset_password_token", length = 30)
    private String resetPasswordToken;

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Customer(Integer id) {
        this.id = id;
    }

}
