package com.freshco.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN(1),
    SELLER(2),
    CUSTOMER(3);

    private final int code;

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static Role fromCode(int code) {
        for (Role role : Role.values()) {
            if (role.code == code) {
                return role;
            }
        }

        throw new IllegalArgumentException("Invalid role code: " + code);
    }
}