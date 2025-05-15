package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    TaskStatus findById(Long id);

    TaskStatus save(TaskStatus taskStatus);

    void delete(Long id);

    TaskStatus findByName(String name);

    List<TaskStatus> findAllByProject(Project project);

    List<TaskStatus> findAll();

}
