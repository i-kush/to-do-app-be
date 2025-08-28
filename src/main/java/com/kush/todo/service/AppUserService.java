package com.kush.todo.service;

import com.kush.todo.dto.CurrentUser;
import com.kush.todo.dto.Permission;
import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.entity.AppUser;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.AppUserMapper;
import com.kush.todo.repository.AppUserRepository;
import com.kush.todo.validator.AppUserValidator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AppUserValidator appUserValidator;
    private final CurrentUser currentUser;

    @Transactional
    public AppUserResponseDto create(AppUserRequestDto appUserRequestDto) {
        appUserValidator.validateTargetRole(appUserRequestDto, currentUser);

        AppUser appUser = appUserMapper.toAppUser(appUserRequestDto, currentUser.getTenantId());
        AppUser createdUser = appUserRepository.save(appUser);
        return appUserMapper.toAppUserDto(createdUser);
    }

    @Transactional(readOnly = true)
    public AppUserResponseDto findByIdRequired(UUID id) {
        return appUserMapper.toAppUserDto(getRequired(id));
    }

    @Transactional(readOnly = true)
    public Optional<AppUserResponseDto> findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                                .map(appUserMapper::toAppUserDto);
    }

    @Transactional
    public AppUserResponseDto update(UUID id, AppUserRequestDto appUserRequestDto) {
        AppUser currentAppUser = getRequired(id);
        appUserValidator.validateTargetRole(appUserRequestDto, currentUser);

        AppUser appUserToUpdate = appUserMapper.toAppUser(currentAppUser, appUserRequestDto);
        AppUser updatedAppUser = appUserRepository.save(appUserToUpdate);
        return appUserMapper.toAppUserDto(updatedAppUser);
    }

    @Transactional
    public void delete(UUID id) {
        appUserValidator.validateDelete(id, currentUser);

        if (!appUserRepository.existsByIdAndTenantId(id, currentUser.getTenantId())) {
            throw new NotFoundException(String.format("No user with id '%s'", id));
        }
        appUserRepository.deleteByIdAndTenantId(id, currentUser.getTenantId());
    }

    private AppUser getRequired(UUID id) {
        return appUserRepository
                .findByIdAndTenantId(id, currentUser.getTenantId())
                .orElseThrow(() -> new NotFoundException(String.format("No user with id '%s'", id)));
    }

    @Transactional(readOnly = true)
    public CustomPage<AppUserResponseDto> findAll(int page, int size) {
        Page<AppUserResponseDto> pages = appUserRepository.findAllByTenantId(PageRequest.of(page - 1, size), currentUser.getTenantId())
                                                          .map(appUserMapper::toAppUserDto);
        return appUserMapper.toCustomPage(pages);
    }

    @Transactional(readOnly = true)
    public List<Permission> findUserPermission(UUID id, UUID tenantId) {
        return appUserRepository.findUserPermissions(id, tenantId);
    }
}