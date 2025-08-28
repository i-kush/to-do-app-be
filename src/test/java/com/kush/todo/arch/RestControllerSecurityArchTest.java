package com.kush.todo.arch;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

class RestControllerSecurityArchTest {

    @Test
    void allControllerMethodsShouldBeSecured() {
        ArchRuleDefinition.methods()
                          .that()
                          .arePublic()
                          .and()
                          .areDeclaredInClassesThat()
                          .areAnnotatedWith(RestController.class)
                          .should()
                          .beAnnotatedWith(PreAuthorize.class)
                          .check(ArchTestSettings.JAVA_CLASSES);
    }
}
