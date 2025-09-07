package com.kush.todo.arch;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

class RestControllerMetricsArchTest {

    private static final List<String> EXCLUDE_METHODS_LIST = List.of(
            "com.kush.todo.controller.AuditController.findAll(int, int)"
    );

    @Test
    void allControllerMethodsShouldHaveTimedMetric() {
        ArchRuleDefinition.methods()
                          .that()
                          .arePublic()
                          .and()
                          .areDeclaredInClassesThat()
                          .areAnnotatedWith(RestController.class)
                          .and()
                          .haveFullNameNotMatching(ArchTestSettings.buildExcludeList(EXCLUDE_METHODS_LIST))
                          .should()
                          .beAnnotatedWith(Timed.class)
                          .andShould(new ArchCondition<>("@Timed should have correct attributes") {
                              @Override
                              public void check(JavaMethod method, ConditionEvents events) {
                                  Timed timed = method.getAnnotationOfType(Timed.class);
                                  if (!StringUtils.hasText(timed.value())) {
                                      ArchTestSettings.addEvent(events, method, String.format("@Timed should have correct metric name for '%s'", method.getFullName()));
                                  }
                                  checkExtraTags(timed.extraTags(), events, method, "@Timed");
                              }
                          }).check(ArchTestSettings.JAVA_CLASSES);
    }

    @Test
    void allControllerMethodsShouldHaveCountedMetric() {
        ArchRuleDefinition.methods()
                          .that()
                          .arePublic()
                          .and()
                          .areDeclaredInClassesThat()
                          .areAnnotatedWith(RestController.class)
                          .and()
                          .haveFullNameNotMatching(ArchTestSettings.buildExcludeList(EXCLUDE_METHODS_LIST))
                          .should()
                          .beAnnotatedWith(Counted.class)
                          .andShould(new ArchCondition<>("@Counted should have correct attributes") {
                              @Override
                              public void check(JavaMethod method, ConditionEvents events) {
                                  Counted counted = method.getAnnotationOfType(Counted.class);
                                  if (!StringUtils.hasText(counted.value())) {
                                      ArchTestSettings.addEvent(events, method, String.format("@Counted should have correct metric name for '%s'", method.getFullName()));
                                  }
                                  checkExtraTags(counted.extraTags(), events, method, "@Counted");
                              }
                          }).check(ArchTestSettings.JAVA_CLASSES);
    }

    private void checkExtraTags(String[] tags, ConditionEvents events, JavaMethod method, String annotation) {
        if (tags == null || tags.length == 0) {
            ArchTestSettings.addEvent(events, method, String.format("%s should have extra tags for '%s'", annotation, method.getFullName()));
        } else if (tags.length % 2 != 0 || tags.length < 4) {
            ArchTestSettings.addEvent(events, method, String.format("%s should have correct amount of extra tags for '%s'", annotation, method.getFullName()));
        } else if (!method.getOwner().getSimpleName().toLowerCase(Locale.getDefault()).contains(tags[1].toLowerCase(Locale.getDefault()))) {
            ArchTestSettings.addEvent(events, method, String.format("%s should have correct domain tag for '%s'", annotation, method.getFullName()));
        }
    }
}
