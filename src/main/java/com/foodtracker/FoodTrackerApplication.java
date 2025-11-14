package com.foodtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@Modulith
@SpringBootApplication
public class FoodTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodTrackerApplication.class, args);
    }
}