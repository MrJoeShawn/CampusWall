package com.campus.wall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.campus.wall", "com.campus.framework"})
@MapperScan("com.campus.framework.dao")
//@MapperScan({"com.campus.wall", "com.campus.framework"})
public class CampusWallApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusWallApplication.class,args);
    }
}
