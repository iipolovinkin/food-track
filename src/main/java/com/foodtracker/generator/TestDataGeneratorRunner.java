package com.foodtracker.generator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * The generator can be run by starting the Spring Boot application with the argument --generate-test-data, which will
 * execute the data generation process and subsequently validate the analytics capabilities of the system.
 */
@Component
public class TestDataGeneratorRunner implements CommandLineRunner {

    private final TestDataManager testDataManager;

    public TestDataGeneratorRunner(TestDataManager testDataManager) {
        this.testDataManager = testDataManager;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only run the generator if a specific argument is provided
        if (args.length > 0 && "--generate-test-data".equals(args[0])) {
            System.out.println("Running Test Data Generator...");
            testDataManager.generateTestData();
        }
    }
}