package com.foodtracker.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class UserGenerator {

    private final Random random = new Random();

    public List<String> generateUsers(int count) {
        List<String> users = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            users.add(generateSingleUser(i));
        }

        return users;
    }

    public String generateSingleUser(int id) {
        return "user_" + String.format("%03d", id);
    }

    public int getRandomUserId(int maxUserId) {
        return random.nextInt(maxUserId) + 1;
    }
}