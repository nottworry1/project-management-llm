package org.kupchenko.projectmanagementllm.controller;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.User;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserService userService;

    @ModelAttribute("allProjects")
    public Set<Project> allProjects() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Prevent infinite redirect for unauthenticated users
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Collections.emptySet();
        }

        final User user = userService.getCurrentUser();
        final Set<Project> projects = new HashSet<>(user.getProjectsMembered());
        projects.addAll(user.getProjectsOwned());
        return projects;
    }
}

