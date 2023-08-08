package com.linkhub.portal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.linkhub.common.mapper")
@ComponentScan(basePackages = {"com.linkhub"})
public class LinkhubPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkhubPortalApplication.class, args);
    }

}
