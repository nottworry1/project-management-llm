package org.kupchenko.projectmanagementllm.controller;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.User;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserService userService;
    private final ProjectService projectService;

    @ModelAttribute("currentProject")
    public Project loadProject(@PathVariable(value = "projectId", required = false) Long projectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isUnauthenticatedOrAnonymous(auth) || projectId == null) {
            return null;
        }
        return projectService.findById(projectId);
    }

    @ModelAttribute("allProjects")
    public List<Project> allProjects() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isUnauthenticatedOrAnonymous(auth)) {
            return Collections.emptyList();
        }

        final Set<Project> projects = getCurrentUserProjects();
        return projects.stream().toList();
    }

    private static boolean isUnauthenticatedOrAnonymous(Authentication auth) {
        return auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser");
    }

    private Set<Project> getCurrentUserProjects() {
        final User user = userService.getCurrentUser();
        final Set<Project> projects = new HashSet<>(user.getProjectsMembered());
        projects.addAll(user.getProjectsOwned());
        return projects;
    }
}

