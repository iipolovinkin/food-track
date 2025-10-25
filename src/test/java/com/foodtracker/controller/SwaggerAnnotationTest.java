package com.foodtracker.controller;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
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
    static final ArchRule controllers_should_use_annotated_dtos =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should(new ArchCondition<JavaClass>("use only documented DTOs") {
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
                                    .peek(type -> log.info("type2: {}", type))
                                    .map(JavaType::toErasure)
                                    .collect(Collectors.toSet());

                            log.info("usedDtos: {}", usedDtos);
                            // Проверяем, что DTO используется
                            for (JavaClass dto : usedDtos) {
                                log.info("dto: {}", dto);
                                // Проверяем, что DTO аннотирован @Schema
                                if (!dto.isAnnotatedWith("Schema")) {
                                    String message = String.format(
                                            "Controller %s uses DTO %s without @Schema annotation",
                                            controller.getSimpleName(),
                                            dto.getSimpleName()
                                    );
                                    events.add(SimpleConditionEvent.violated(controller, message));
                                }
                                log.info(Schema.class.getName());
                                // Проверяем поля DTO
                                Set<String> undocumentedFields = dto.getFields().stream()
                                        .filter(field -> !field.isAnnotatedWith(Schema.class.getName()))
                                        .map(JavaMember::getName)
                                        .collect(Collectors.toSet())
                                        ;
//                                        .count();

                                Set<JavaField> schemas = dto.getFields().stream()
//                                        .filter(f->f.isStatiFinal())
                                        .filter(field -> field.isAnnotatedWith(Schema.class))
                                        .peek(this::check)
                                        .collect(Collectors.toSet());
                                log.info("schemas: {}", schemas);
                                if (!undocumentedFields.isEmpty()) {
                                    String message = String.format(
                                            "DTO %s has %d fields without @Schema annotation, %s",
                                            dto.getSimpleName(), undocumentedFields.size(), undocumentedFields
                                    );
                                    events.add(SimpleConditionEvent.violated(controller, message));
                                }
                            }
                        }

                        //                                    if ()
                        private void check(JavaField javaField) {
                            log.info("javaField: {}", javaField.getName());
                            javaField.getAnnotations().stream()
                                    .forEach(annotation -> {
                                log.info("annotation: {}", annotation);
                                annotation.getProperties()
                                        .entrySet()
                                        .stream()
                                        .filter(f-> Objects.nonNull(f.getValue()))
                                        .filter(f-> !(f instanceof Collection) || !((Collection) f).isEmpty())
                                        .forEach(e -> {
                                    log.info("k: {}, v: {}", e.getKey(), e.getValue());
                                });
                            });
                        }
                    });
}