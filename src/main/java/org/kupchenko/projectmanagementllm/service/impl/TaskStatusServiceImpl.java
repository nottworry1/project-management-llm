package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.TaskStatus;
import org.kupchenko.projectmanagementllm.repository.TaskStatusRepository;
import org.kupchenko.projectmanagementllm.service.TaskStatusService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus findById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus with id %d not found".formatted(id)));
    }

    @Override
    public TaskStatus save(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }

    @Override
    public TaskStatus findByName(String name) {
        return taskStatusRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus with name %s not found".formatted(name)));
    }

    @Override
    public List<TaskStatus> findAllByProject(Project project) {
        return null;
    }

    @Override
    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }
}
