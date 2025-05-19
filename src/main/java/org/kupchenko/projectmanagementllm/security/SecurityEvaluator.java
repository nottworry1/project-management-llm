package org.kupchenko.projectmanagementllm.security;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.*;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SecurityEvaluator {
    private final UserService userService;

    public boolean canAccessTask(Long taskId) {
        Set<Project> projects = getCurrentUserProjects();

        return projects.stream()
                .flatMap(project -> project.getTasks().stream())
                .map(Task::getId)
                .toList().contains(taskId);
    }

    public boolean canAccessBoard(Long boardId) {
        Set<Project> projects = getCurrentUserProjects();

        return projects.stream()
                .flatMap(project -> project.getBoards().stream())
                .map(Board::getId)
                .toList().contains(boardId);
    }

    public boolean canAccessProject(Long projectId) {
        Set<Project> projects = getCurrentUserProjects();

        return projects.stream()
                .map(Project::getId)
                .toList().contains(projectId);
    }

    public boolean canAccessSprint(Long sprintId) {
        Set<Project> projects = getCurrentUserProjects();

        return projects.stream()
                .flatMap(project -> project.getSprints().stream())
                .map(Sprint::getId)
                .toList().contains(sprintId);
    }

    private Set<Project> getCurrentUserProjects() {
        User user = userService.getCurrentUser();
        Set<Project> projects = new HashSet<>(user.getProjectsMembered());
        projects.addAll(user.getProjectsOwned());
        return projects;
    }
}
