package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Project;

import java.util.List;

public interface ProjectService {
    List<Project> findAll();
    Project findById(Long id);
    Project save(Project project);
    Project update(Project project);
    void deleteById(Long id);
}

