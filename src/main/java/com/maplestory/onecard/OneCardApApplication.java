package com.maplestory.onecard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class OneCardApApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneCardApApplication.class, args);
    }

}
