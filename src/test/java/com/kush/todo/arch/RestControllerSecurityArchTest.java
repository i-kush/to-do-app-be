package com.kush.todo.arch;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

class RestControllerSecurityArchTest {

    private static final List<String> EXCLUDE_METHODS_LIST = List.of(
            "com.kush.todo.controller.AppUserController.me()",
            "com.kush.todo.controller.AuthController.login(com.kush.todo.dto.request.LoginRequestDto)",
            "com.kush.todo.controller.AsyncOperationController.getOperation(java.util.UUID)"
    );

    @Test
    void allControllerMethodsShouldBeSecured() {
        ArchRuleDefinition.methods()
                          .that()
                          .arePublic()
                          .and()
                          .areDeclaredInClassesThat()
                          .areAnnotatedWith(RestController.class)
                          .and()
                          .haveFullNameNotMatching(ArchTestSettings.buildExcludeList(EXCLUDE_METHODS_LIST))
                          .should()
                          .beAnnotatedWith(PreAuthorize.class)
                          .check(ArchTestSettings.JAVA_CLASSES);
    }
}
