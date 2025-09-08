package com.kush.todo.service;

import com.kush.todo.config.RedisConfig;
import com.kush.todo.constant.Messages;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.common.Permission;
import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.entity.AppUser;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.AppUserMapper;
import com.kush.todo.repository.AppUserRepository;
import com.kush.todo.validator.AppUserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    public static final String ERROR_MESSAGE_USER_IS_NOT_LOCKED = "User is not locked";

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AppUserValidator appUserValidator;
    private final CurrentUser currentUser;
    @Value("${todo.login.max-attempts}")
    private int maxLoginAttempts;
    @Value("${todo.login.max-attempts-window-minutes}")
    private int maxLoginAttemptWindowMinutes;

    @Transactional
    public AppUserResponseDto createFirstAdmin(UUID tenantId, String adminEmail) {
        return create(appUserMapper.toFirstAdmin(adminEmail), tenantId);
    }

    @Transactional
    public AppUserResponseDto create(AppUserRequestDto request) {
        return create(request, currentUser.getTenantId());
    }

    private AppUserResponseDto create(AppUserRequestDto request, UUID tenantId) {
        appUserValidator.validateTargetRole(request, currentUser);
        AppUser appUser = appUserMapper.toAppUser(request, tenantId);

        return appUserMapper.toAppUserDto(appUserRepository.save(appUser));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = RedisConfig.CACHE_NAME_USERS, key = "#id")
    public AppUserResponseDto findByIdRequired(UUID id) {
        return appUserMapper.toAppUserDto(appUserRepository.findByIdAndTenantIdRequired(id, currentUser.getTenantId()));
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_NAME_USERS, key = "#id")
    public AppUserResponseDto update(UUID id, AppUserRequestDto request) {
        AppUser currentAppUser = appUserRepository.findByIdAndTenantIdRequired(id, currentUser.getTenantId());
        appUserValidator.validateTargetRole(request, currentUser);
        AppUser appUser = appUserMapper.toAppUser(currentAppUser, request);

        return appUserMapper.toAppUserDto(appUserRepository.save(appUser));
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_NAME_USERS, key = "#id")
    public void delete(UUID id) {
        appUserValidator.validateDelete(id, currentUser);

        if (!appUserRepository.existsByIdAndTenantId(id, currentUser.getTenantId())) {
            throw new NotFoundException(String.format(Messages.PATTERN_NOT_FOUND, id));
        }
        appUserRepository.deleteByIdAndTenantId(id, currentUser.getTenantId());
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = RedisConfig.CACHE_NAME_USERS, key = "#appUser.id")
    public void lockUserIfNeeded(AppUser appUser) {
        boolean isAttemptWithinWindow = Optional.ofNullable(appUser.lastLoginAttemptAt())
                                                .map(a -> a.isAfter(Instant.now().minus(Duration.ofMinutes(maxLoginAttemptWindowMinutes))))
                                                .orElse(true);
        int loginAttempts = appUser.loginAttempts() == null ? 1 : appUser.loginAttempts() + 1;
        if (!isAttemptWithinWindow) {
            loginAttempts = 1;
        }

        if (loginAttempts >= maxLoginAttempts) {
            log.info("Locking user {} due to {} invalid attempts within {} minutes", appUser.id(), maxLoginAttempts, maxLoginAttemptWindowMinutes);
            appUserRepository.lockUser(appUser.id(), appUser.tenantId());
        } else {
            appUserRepository.incrementLoginAttempts(appUser.id(), appUser.tenantId(), loginAttempts);
        }
    }

    @Transactional
    public void nullifyLoginAttempts(AppUser appUser) {
        appUserRepository.nullifyLoginAttempts(appUser.id(), appUser.tenantId());
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_NAME_USERS, allEntries = true)
    public void unlockUsers() {
        Set<UUID> ids = appUserRepository.findUserIdsToUnlock();
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("About to unlock {} users", ids.size());
            appUserRepository.unlockUsers(ids);
        }
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_NAME_USERS, key = "#id")
    public void unlockUser(UUID id) {
        if (!appUserRepository.findByIdAndTenantIdRequired(id, currentUser.getTenantId()).isLocked()) {
            throw new IllegalArgumentException(ERROR_MESSAGE_USER_IS_NOT_LOCKED);
        }

        appUserRepository.unlockUser(id, currentUser.getTenantId());
    }

    @Transactional(readOnly = true)
    public boolean isCurrentUserLocked() {
        return appUserRepository.isUserLocked(currentUser.getId(), currentUser.getTenantId())
                                .orElse(true);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_NAME_USERS, allEntries = true)
    public int deleteByTenantId(UUID tenantId) {
        return appUserRepository.deleteByTenantId(tenantId);
    }
}