package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.dto.RegistrationForm;
import org.kupchenko.projectmanagementllm.model.User;

public interface UserService {
    User findById(long id);

    User findByEmail(String email);

    void registerUser(RegistrationForm form);

    boolean emailExists(String email);

    User getCurrentUser();
}
