package com.sportsmeet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sportsmeet.mapper")
public class SportsMeetApplication {
    public static void main(String[] args) {
        SpringApplication.run(SportsMeetApplication.class, args);
    }
}
