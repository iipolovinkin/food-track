package com.foodtracker.generator;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class SessionGenerator {
    
    private final Random random = new Random();
    
    public String generateSessionId() {
        return "sess_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    public int generateSessionCount(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}