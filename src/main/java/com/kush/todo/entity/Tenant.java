package com.kush.todo.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Tenant extends CommonEntity {

    private String name;
}
