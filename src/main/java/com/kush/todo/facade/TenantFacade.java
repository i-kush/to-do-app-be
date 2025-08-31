package com.kush.todo.facade;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.service.TenantService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantFacade {

    private final TenantService tenantService;

    public TenantResponseDto create(TenantRequestDto request) {
        //ToDo https://github.com/i-kush/to-do-app-be/issues/23 introduce onboarding
        return tenantService.create(request);
    }
}
