package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Project;

import java.util.List;

public interface ProjectService {
    List<Project> findAll();
    Project findById(Long id);
    Project save(Project project);
    Project update(Project project);
    void deleteById(Long id);

    void addMember(Long projectId, String username);

    void promoteToOwner(Long projectId, Long userId);

    void demoteOwner(Long projectId, Long userId);

    void removeMember(Long projectId, Long userId);
}

