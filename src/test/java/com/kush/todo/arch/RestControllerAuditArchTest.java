package com.kush.todo.arch;

import com.kush.todo.annotation.Auditable;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import java.util.Locale;

class RestControllerAuditArchTest {

    @Test
    void allAuditableMethodsShouldHaveCorrectTargetType() {
        ArchRuleDefinition
                .methods()
                .that()
                .areAnnotatedWith(Auditable.class)
                .should(new ArchCondition<>("@Auditable should have correct target type") {
                    @Override
                    public void check(JavaMethod method, ConditionEvents events) {
                        Auditable auditable = method.getAnnotationOfType(Auditable.class);
                        if (!method.getOwner().getSimpleName().toLowerCase(Locale.getDefault()).contains(getDomainPart(auditable))) {
                            ArchTestSettings.addEvent(events, method, String.format("@Auditable should have correct target type for '%s'", method.getFullName()));
                        }
                    }
                })
                .check(ArchTestSettings.JAVA_CLASSES);
    }

    private String getDomainPart(Auditable auditable) {
        return auditable.targetType().toString().split("_")[0].toLowerCase(Locale.getDefault());
    }
}
