package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.exception.UserAlreadyMemberOfProjectException;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.User;
import org.kupchenko.projectmanagementllm.repository.ProjectRepository;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id %d ".formatted(id)));
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project update(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public void addMember(Long projectId, String username) {
        Project project = findById(projectId);
        User user = userService.findByUsername(username);
        verifyUserAlreadyMemberOfProject(project, user);
        project.getUsers().add(user);
        projectRepository.save(project);
    }

    @Override
    public void promoteToOwner(Long projectId, Long userId) {
        Project project = findById(projectId);
        User user = userService.findById(userId);
        verifyUserAlreadyOwnerOfProject(project, user);
        project.getOwners().add(user);
        projectRepository.save(project);
    }

    @Override
    public void demoteOwner(Long projectId, Long userId) {
        Project project = findById(projectId);
        User user = userService.findById(userId);
        project.getOwners().remove(user);
        projectRepository.save(project);
    }

    @Override
    public void removeMember(Long projectId, Long userId) {
        Project project = findById(projectId);
        User user = userService.findById(userId);
        project.getUsers().remove(user);
        project.getOwners().remove(user);
        projectRepository.save(project);
    }

    private static void verifyUserAlreadyMemberOfProject(Project project, User user) {
        if (project.getUsers().contains(user) || project.getOwners().contains(user)) {
            throw new UserAlreadyMemberOfProjectException("User %s is already a member of project %s".formatted(user.getUsername(), project.getName()));
        }
    }

    private static void verifyUserAlreadyOwnerOfProject(Project project, User user) {
        if (project.getOwners().contains(user)) {
            throw new UserAlreadyMemberOfProjectException("User %s is already an owner of project %s".formatted(user.getUsername(), project.getName()));
        }
    }
}