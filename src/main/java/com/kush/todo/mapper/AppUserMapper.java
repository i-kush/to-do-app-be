package com.kush.todo.mapper;

import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AppUserMapper extends PageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = MappingConstants.TIMESTAMP_NOW_EXPRESSION)
    @Mapping(target = "updated", expression = MappingConstants.TIMESTAMP_NOW_EXPRESSION)
    public abstract AppUser toAppUser(AppUserRequestDto appUserRequestDto);

    @Mapping(target = "id", source = "appUser.id")
    @Mapping(target = "username", source = "appUserRequestDto.username")
    @Mapping(target = "passwordHash", source = "appUserRequestDto.password")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "firstname", ignore = true)
    @Mapping(target = "lastname", ignore = true)
    @Mapping(target = "created", source = "appUser.created")
    @Mapping(target = "updated", expression = MappingConstants.TIMESTAMP_NOW_EXPRESSION)
    public abstract AppUser toAppUser(AppUser appUser, AppUserRequestDto appUserRequestDto);

    public abstract AppUserResponseDto toAppUserDto(AppUser appUser);
}
