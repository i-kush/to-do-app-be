package com.kush.todo.mapper;

import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper
public abstract class AppUserMapper extends PageMapper {

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", expression = MappingConstants.EXPRESSION_PASSWORD_HASH)
    @Mapping(target = "isLocked", constant = "false")
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "loginAttempts", ignore = true)
    @Mapping(target = "lastLoginAttemptAt", ignore = true)
    @Mapping(target = "createdAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract AppUser toAppUser(AppUserRequestDto appUserRequestDto, UUID tenantId);

    @Mapping(target = "id", source = "appUser.id")
    @Mapping(target = "username", source = "appUserRequestDto.username")
    @Mapping(target = "passwordHash", expression = MappingConstants.EXPRESSION_PASSWORD_HASH)
    @Mapping(target = "email", source = "appUserRequestDto.email")
    @Mapping(target = "roleId", source = "appUserRequestDto.roleId")
    @Mapping(target = "firstname", source = "appUserRequestDto.firstname")
    @Mapping(target = "lastname", source = "appUserRequestDto.lastname")
    @Mapping(target = "loginAttempts", source = "appUser.loginAttempts")
    @Mapping(target = "lastLoginAttemptAt", source = "appUser.lastLoginAttemptAt")
    @Mapping(target = "lockedAt", source = "appUser.lockedAt")
    @Mapping(target = "createdAt", source = "appUser.createdAt")
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract AppUser toAppUser(AppUser appUser, AppUserRequestDto appUserRequestDto);

    public abstract AppUserResponseDto toAppUserDto(AppUser appUser);
}
