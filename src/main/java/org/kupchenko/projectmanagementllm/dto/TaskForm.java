package org.kupchenko.projectmanagementllm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.kupchenko.projectmanagementllm.model.Task;

@Data
public class TaskForm {
    private Long id;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private Long taskStatusId;
    @NotNull
    private Long assigneeId;
    @NotNull
    private Long reporterId;
    @NotNull
    private Long projectId;
    @NotNull
    private Task.Priority priority;

    public static TaskForm fromEntity(Task task) {
        TaskForm f = new TaskForm();
        f.setId(task.getId());
        f.setTitle(task.getTitle());
        f.setDescription(task.getDescription());
        f.setTaskStatusId(task.getTaskStatus().getId());
        f.setAssigneeId(task.getAssignee().getId());
        f.setReporterId(task.getReporter().getId());
        f.setProjectId(task.getProject().getId());
        f.setPriority(task.getPriority());
        return f;
    }
}
