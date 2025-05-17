package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.dto.RegistrationForm;
import org.kupchenko.projectmanagementllm.model.User;

import java.util.List;

public interface UserService {
    User findById(long id);

    User findByEmail(String email);

    List<User> findAll();

    User findByUsername(String username);

    void registerUser(RegistrationForm form);

    boolean emailExists(String email);

    User getCurrentUser();
}
