package com.kush.todo.controller;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.service.TenantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponseDto create(@Valid @NotNull @RequestBody TenantRequestDto tenantDto) {
        return tenantService.create(tenantDto);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TenantResponseDto get(@NotNull @PathVariable UUID id) {
        return tenantService.findById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<TenantResponseDto> getAll(@RequestParam @Min(1) int page,
                                                @RequestParam @Min(1) @Max(200) int size) {
        return tenantService.findAll(page, size);
    }

    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TenantResponseDto update(@NotNull @PathVariable UUID id,
                                    @Valid @NotNull @RequestBody TenantRequestDto tenantDto) {
        return tenantService.update(id, tenantDto);
    }

    @DeleteMapping("{id}")
    public void delete(@NotNull @PathVariable UUID id) {
        tenantService.delete(id);
    }
}
