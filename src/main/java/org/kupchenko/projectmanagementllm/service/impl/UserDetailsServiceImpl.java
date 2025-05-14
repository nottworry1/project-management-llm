package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.User;
import org.kupchenko.projectmanagementllm.model.UserDetailsImpl;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = userService.findByEmail(username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return UserDetailsImpl.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .grantedAuthority(user.getRoles().stream()
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getName()))
                        .toList())
                .build();
    }
}
