package com.campus.framework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.campus.framework.dao.mapper")
@SpringBootApplication
public class FrameworkApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrameworkApplication.class, args);
    }
}
