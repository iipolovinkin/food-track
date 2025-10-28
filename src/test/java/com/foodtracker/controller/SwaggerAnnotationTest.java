package com.foodtracker.controller;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "com.foodtracker.controller")
@Slf4j
public class SwaggerAnnotationTest {

    @ArchTest
    static final ArchRule rest_controllers_should_have_swagger_annotations =
            methods()
                    .that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                    .and().arePublic()
                    .should().beAnnotatedWith(Operation.class)
                    .because("Все публичные методы контроллеров должны документироваться в Swagger");


    @ArchTest
    static final ArchRule controllers_should_use_annotated_dtos;

    static {
        ArchCondition<JavaClass> condition = new ArchCondition<>("use only documented DTOs") {
            @Override
            public void check(JavaClass controller, ConditionEvents events) {
                log.info("controller: {}", controller);
                // Получаем все DTO классы, используемые в контроллере (в параметрах и возвращаемых типах)
                Set<JavaClass> usedDtos = controller.getCodeUnits().stream()
                        .flatMap(method -> {
                            Stream<JavaType> parameterTypes = method.getParameters().stream()
                                    .map(JavaParameter::getType);
                            JavaType returnType = method.getReturnType();
                            return Stream.concat(parameterTypes, Stream.of(returnType));
                        })
                        .filter(type -> type.toErasure().getPackageName().contains(".dto"))
                        .map(JavaType::toErasure)
                        .collect(Collectors.toSet());

                log.info("usedDtos: {}", usedDtos);
                // Проверяем, что DTO используется
                for (JavaClass dto : usedDtos) {
                    validate(controller, dto).forEach(events::add);
                }
            }

            private List<ConditionEvent> validate(JavaClass controller, JavaClass dto) {
                List<ConditionEvent> errors = new ArrayList<>();
                log.info("dto: {}", dto);
                // Проверяем, что DTO аннотирован @Schema
                if (!dto.isAnnotatedWith(Schema.class)) {
                    String message = String.format(
                            "Controller %s uses DTO %s without @Schema annotation",
                            controller.getSimpleName(),
                            dto.getSimpleName()
                    );
                    errors.add(SimpleConditionEvent.violated(controller, message));
                }
                log.info(Schema.class.getName());
                // Проверяем поля DTO
                dto.getFields().stream()
                        .map(f -> validateField(f, dto.getSimpleName()))
                        .filter(Objects::nonNull)
                        .map(m -> SimpleConditionEvent.violated(controller, m))
                        .forEach(errors::add);
                return errors;
            }
        };
        controllers_should_use_annotated_dtos = classes()
                .that().areAnnotatedWith(RestController.class)
                .should(condition)
                .because("Все публичные методы контроллеров должны документироваться в Swagger");
    }

    private static String validateField(JavaField field, String className) {
        Optional<Schema> schema = field.tryGetAnnotationOfType(Schema.class);
        if (schema.isEmpty()) {
            return String.format(
                    "DTO %s has field %s without @Schema annotation",
                    className, field.getName()
            );
        }
        return schema
                .map(SwaggerAnnotationTest::validateFieldSchema)
                .map(m -> String.format(
                        "DTO %s has field %s with @Schema annotation, but %s",
                        className, field.getName(), m))
                .orElse(null);
    }

    private static String validateFieldSchema(Schema a) {
        List<String> errors = new ArrayList<>();
        if (!StringUtils.hasText(a.description())) {
            errors.add("description should be not empty");
        }
        if (!StringUtils.hasText(a.example())
                && ArrayUtils.isEmpty(a.examples())
                && ArrayUtils.isEmpty(a.exampleClasses())) {
            errors.add("example should be not empty");
        }
        return errors.isEmpty() ? null : String.join(", ", errors);
    }
}