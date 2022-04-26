package com.densoft.shopmefrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.densoft.shopmecommon.entity","com.densoft.shopmeAdmin.user"})
public class ShopmeFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopmeFrontendApplication.class, args);
    }

}
