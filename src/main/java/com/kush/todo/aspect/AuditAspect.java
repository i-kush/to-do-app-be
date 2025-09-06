package com.kush.todo.aspect;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

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
            auditService.create(currentUser.getId(), targetId, audit);
            return result;
        } catch (Throwable e) {
            auditService.create(currentUser.getId(), targetId, audit, e);
            throw e;
        }
    }

    private UUID tryGetTargetId(Object[] args) {
        //ToDo add actual target ID extraction
        return UUID.randomUUID();
    }
}
