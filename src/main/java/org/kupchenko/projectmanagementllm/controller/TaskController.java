package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.dto.TaskForm;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.model.User;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.kupchenko.projectmanagementllm.service.TaskService;
import org.kupchenko.projectmanagementllm.service.TaskStatusService;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskStatusService taskStatusService;
    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public String list(@PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("tasks", taskService.findAllByProject(project));
        return "tasks/index";
    }

    @GetMapping("/create")
    public String createForm(@PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId);

        TaskForm form = new TaskForm();
        form.setProjectId(projectId);
        model.addAttribute("taskForm", form);

        model.addAttribute("users", mergeMembersAndOwners(project));
        model.addAttribute("taskStatuses", taskStatusService.findAll());
        return "tasks/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("taskForm") TaskForm form,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            // re-populate selects
            Project project = projectService.findById(form.getProjectId());
            model.addAttribute("users", mergeMembersAndOwners(project));
            model.addAttribute("taskStatuses", taskStatusService.findAll());
            return "tasks/form";
        }

        Task task = toEntity(form);
        taskService.save(task);
        return "redirect:/projects/" + form.getProjectId() + "/tasks";
    }

    @GetMapping("/{taskId}/edit")
    public String editForm(@PathVariable Long projectId,
                           @PathVariable Long taskId,
                           Model model) {
        Task task = taskService.findById(taskId);

        if (task.getProject().getTasks().contains(task)) {
            model.addAttribute("project", task.getProject());
        } else {
            throw new IllegalArgumentException("Task does not belong to the project");
        }

        TaskForm form = TaskForm.fromEntity(task);
        form.setProjectId(projectId);

        model.addAttribute("taskForm", form);
        model.addAttribute("users", mergeMembersAndOwners(task.getProject()));
        model.addAttribute("taskStatuses", taskStatusService.findAll());
        return "tasks/form";
    }

    @PostMapping("/{taskId}")
    public String update(@Valid @ModelAttribute("taskForm") TaskForm form,
                         BindingResult br,
                         @PathVariable Long taskId,
                         Model model) {
        if (br.hasErrors()) {
            Project project = projectService.findById(form.getProjectId());
            model.addAttribute("users", mergeMembersAndOwners(project));
            model.addAttribute("taskStatuses", taskStatusService.findAll());
            return "tasks/form";
        }

        Task task = toEntity(form);
        task.setId(taskId);
        taskService.save(task);
        return "redirect:/projects/" + form.getProjectId() + "/tasks";
    }

    private List<User> mergeMembersAndOwners(Project project) {
        List<User> list = new ArrayList<>(project.getUsers());
        list.addAll(project.getOwners());
        return list;
    }

    @PostMapping("/{taskId}/delete")
    public String delete(@PathVariable Long projectId,
                         @PathVariable Long taskId) {
        taskService.delete(taskId);
        return "redirect:/projects/" + projectId + "/tasks";
    }

    @GetMapping("/{taskId}")
    public String details(@PathVariable Long projectId,
                          @PathVariable Long taskId,
                          Model model) {
        Task task = taskService.findById(taskId);
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("task", task);
        return "tasks/details";
    }

    private Task toEntity(TaskForm form) {
        final Task task;
        if (form.getId() != null) {
            task = taskService.findById(form.getId());
        } else {
            task = new Task();
        }

        task.setTitle(form.getTitle());
        task.setDescription(form.getDescription());
        task.setPriority(form.getPriority());

        task.setTaskStatus(taskStatusService.findById(form.getTaskStatusId()));
        task.setAssignee(userService.findById(form.getAssigneeId()));
        task.setReporter(userService.findById(form.getReporterId()));
        task.setProject(projectService.findById(form.getProjectId()));

        return task;
    }
}


