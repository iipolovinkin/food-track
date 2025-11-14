package com.foodtracker;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class FoodTrackerApplicationTest {

    @Test
    void createApplicationModuleModel() {
        ApplicationModules modules = ApplicationModules.of(FoodTrackerApplication.class);

        modules.verify();

        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml()
                .writeModulesAsPlantUml()

        ;

    }
}
