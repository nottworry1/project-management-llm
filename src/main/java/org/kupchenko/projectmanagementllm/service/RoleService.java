package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Role;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleService {
    Role findByName(String name);
}
