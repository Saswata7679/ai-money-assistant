package com.moneyassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MoneyAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyAssistantApplication.class, args);
    }
}
