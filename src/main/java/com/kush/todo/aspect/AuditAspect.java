package com.kush.todo.aspect;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final CurrentUser currentUser;

    @Around("@annotation(audit)")
    public Object aroundAuditedMethod(ProceedingJoinPoint pjp, Auditable audit) throws Throwable {
        UUID targetId = tryGetTargetId(pjp.getArgs());

        try {
            Object result = pjp.proceed();
            auditService.create(currentUser.getTenantId(), currentUser.getId(), targetId, audit);
            return result;
        } catch (Exception e) {
            auditService.create(currentUser.getTenantId(), currentUser.getId(), targetId, audit, e);
            throw e;
        }
    }

    private UUID tryGetTargetId(Object[] args) {
        return Optional.ofNullable(args)
                       .filter(a -> a.length > 0)
                       .map(a -> a[0])
                       .filter(UUID.class::isInstance)
                       .map(UUID.class::cast)
                       .orElse(null);
    }
}
