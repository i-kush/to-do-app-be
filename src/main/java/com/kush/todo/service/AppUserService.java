package com.kush.todo.service;

import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.entity.AppUser;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.AppUserMapper;
import com.kush.todo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;

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

    @Transactional
    public AppUserResponseDto create(AppUserRequestDto appUserRequestDto) {
        AppUser appUser = appUserMapper.toAppUser(appUserRequestDto, UUID.fromString("8cd702dc-fb77-4854-8192-3cb8b92def41")); //ToDo use tenant ID from the context
        AppUser createdUser = appUserRepository.save(appUser);
        return appUserMapper.toAppUserDto(createdUser);
    }

    @Transactional(readOnly = true)
    public AppUserResponseDto findById(UUID id) {
        return appUserMapper.toAppUserDto(getRequired(id));
    }

    @Transactional
    public AppUserResponseDto update(UUID id, AppUserRequestDto appUserRequestDto) {
        AppUser appUser = appUserMapper.toAppUser(getRequired(id), appUserRequestDto);
        AppUser updatedAppUser = appUserRepository.save(appUser);
        return appUserMapper.toAppUserDto(updatedAppUser);
    }

    @Transactional
    public void delete(UUID id) {
        if (!appUserRepository.existsById(id)) {
            throw new NotFoundException(String.format("No user with id '%s'", id));
        }
        appUserRepository.deleteById(id);
    }

    private AppUser getRequired(UUID id) {
        return appUserRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id '%s'", id)));
    }

    @Transactional(readOnly = true)
    public CustomPage<AppUserResponseDto> findAll(int page, int size) {
        Page<AppUserResponseDto> pages = appUserRepository.findAll(PageRequest.of(page - 1, size))
                                                          .map(appUserMapper::toAppUserDto);
        return appUserMapper.toCustomPage(pages);
    }
}