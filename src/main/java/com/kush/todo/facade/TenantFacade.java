package com.kush.todo.facade;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.AsyncOperationLaunchedResponseDto;
import com.kush.todo.dto.response.AsyncOperationResultResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.service.AsyncOperationService;
import com.kush.todo.service.TenantService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantFacade {

    private final TenantService tenantService;
    private final AsyncOperationService asyncOperationService;

    @Transactional
    public TenantResponseDto create(TenantRequestDto request) {
        //ToDo https://github.com/i-kush/to-do-app-be/issues/23 introduce onboarding
        return tenantService.create(request);
    }

    public AsyncOperationResultResponseDto<TenantResponseDto> getAsyncResult(UUID id) {
        return asyncOperationService.get(id);
    }

    public AsyncOperationLaunchedResponseDto createAsync(TenantRequestDto tenantRequestDto) {
        return asyncOperationService.launch(tenantRequestDto, "test");
    }
}
