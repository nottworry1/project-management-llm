package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Role;
import org.kupchenko.projectmanagementllm.repository.RoleRepository;
import org.kupchenko.projectmanagementllm.service.RoleService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found by name %s".formatted(name)));
    }
}
