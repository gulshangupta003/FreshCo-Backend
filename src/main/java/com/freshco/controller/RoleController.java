package com.freshco.controller;

import com.freshco.dto.response.RoleDto;
import com.freshco.entity.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @GetMapping
    public ResponseEntity<List<RoleDto>> getRoles() {
        List<RoleDto> roles = Arrays.stream(Role.values())
                .map(role -> new RoleDto(role.name(), role.getCode()))
                .toList();

        return ResponseEntity.ok(roles);
    }

    @GetMapping("/map")
    public Map<String, Integer> getRoleMap() {
        Map<String, Integer> roles = new LinkedHashMap<>();
        for (Role role : Role.values()) {
            roles.put(role.name(), role.getCode());
        }

        return roles;
    }

}
