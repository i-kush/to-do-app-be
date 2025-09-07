//package com.kush.todo.arch;
//
//import com.tngtech.archunit.core.domain.JavaMethod;
//import com.tngtech.archunit.lang.ArchCondition;
//import com.tngtech.archunit.lang.ConditionEvents;
//import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
//import io.micrometer.core.annotation.Timed;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Locale;
//
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.RestController;
//
//class RestControllerMetricsArchTest {
//
//    private static final List<String> EXCLUDE_METHODS_LIST = List.of(
//    );
//
//    @Test
//    void allControllerMethodsShouldBeSecured() {
//        ArchRuleDefinition.methods()
//                          .that()
//                          .arePublic()
//                          .and()
//                          .areDeclaredInClassesThat()
//                          .areAnnotatedWith(RestController.class)
//                          .and()
//                          .haveFullNameNotMatching(ArchTestSettings.buildExcludeList(EXCLUDE_METHODS_LIST))
//                          .should()
//                          .beAnnotatedWith(Timed.class)
//                          .andShould(new ArchCondition<>("@Auditable should have correct target type") {
//                              @Override
//                              public void check(JavaMethod method, ConditionEvents events) {
//                                  Timed timed = method.getAnnotationOfType(Timed.class);
//                                  if (!StringUtils.hasText(timed.value())) {
//                                      ArchTestSettings.addEvent(events, method, String.format("@Timed should have correct metric name for '%s'", method.getFullName()));
//                                  }
//
//                                  verifyExtraTags(timed.extraTags(), events, method);
//
//                                  if (!method.getOwner().getSimpleName().toLowerCase(Locale.getDefault())
//                                             .contains(getDomainPart(auditable))) {
//                                      ArchTestSettings.addEvent(events, method, String.format("@Auditable should have correct target type for '%s'", method.getFullName()));
//                                  }
//                              }
//                          }).check(ArchTestSettings.JAVA_CLASSES);
//    }
//
//
//    private void verifyExtraTags(String[] strings, ConditionEvents events, JavaMethod method) {
//        ArchTestSettings.addEvent(events, method, String.format("@Timed should have correct metric name for '%s'", method.getFullName()));
//    }
//}
