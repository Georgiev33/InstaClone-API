package com.example.demo.service;

import com.example.demo.model.entity.Role;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    Role findRole(String role) {
        return repository.findByAuthority(role).orElseThrow(() -> new NotFoundException("Role not found"));
    }
}