package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.dto.RegistrationForm;
import org.kupchenko.projectmanagementllm.model.Role;
import org.kupchenko.projectmanagementllm.model.User;
import org.kupchenko.projectmanagementllm.model.UserDetailsImpl;
import org.kupchenko.projectmanagementllm.repository.UserRepository;
import org.kupchenko.projectmanagementllm.service.RoleService;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by ID %d".formatted(id)));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found by email %s".formatted(email)));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found by username %s".formatted(username)));
    }

    @Override
    public void registerUser(RegistrationForm form) {
        String email = form.getEmail();

        if (emailExists(email)) {
            throw new EntityExistsException("User with email %s already exists".formatted(email));
        }

        Role role = roleService.findByName("ROLE_USER");
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setEmail(email);
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getCurrentUser() {
        final Object authentication = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (authentication instanceof UserDetailsImpl userDetails) {
            return findByEmail(userDetails.getUsername());
        }
        return null;
    }
}
