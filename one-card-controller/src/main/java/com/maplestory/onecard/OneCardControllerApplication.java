package com.maplestory.onecard;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.maplestory.onecard")
@MapperScan(basePackages = "com.maplestory.onecard.model.mapper")
@Slf4j
public class OneCardControllerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(OneCardControllerApplication.class);
            app.run(args);
            log.info("[admin-server]: started");
        } catch (Exception e) {
            log.error("[admin-server]: error={}", e.getMessage(), e);
        }
    }

}
