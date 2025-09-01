package com.kush.todo.dto.common;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

//Cannot be a record/immutable class due to proxy related stuff
@Getter
@Builder
public class CurrentUser {
    private UUID id;
    private UUID tenantId;
    private Role role;
    private String username;
    private String email;
}
