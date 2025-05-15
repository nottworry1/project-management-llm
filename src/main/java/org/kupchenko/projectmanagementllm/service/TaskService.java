package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAllByProject(Project project);
    Task findById(Long id);
    Task save(Task task);
    void delete(Long id);
}
