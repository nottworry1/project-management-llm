package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.dto.CommentForm;
import org.kupchenko.projectmanagementllm.dto.TaskForm;
import org.kupchenko.projectmanagementllm.model.*;
import org.kupchenko.projectmanagementllm.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskStatusService taskStatusService;
    private final ProjectService projectService;
    private final UserService userService;
    private final CommentService commentService;
    private final SprintService sprintService;
    private final LabelService labelService;

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping
    public String list(@PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("tasks", taskService.findAllByProject(project));
        return "tasks/index";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
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

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping
    public String create(@PathVariable Long projectId,
                         @Valid @ModelAttribute("taskForm") TaskForm form,
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
        return "redirect:/projects/" + form.getProjectId() + "/tasks/" + task.getId();
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
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

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{taskId}")
    public String update(@Valid @ModelAttribute("taskForm") TaskForm form,
                         BindingResult br,
                         @PathVariable Long taskId,
                         @PathVariable Long projectId,
                         Model model) {
        if (br.hasErrors()) {
            Project project = projectService.findById(form.getProjectId());
            model.addAttribute("users", mergeMembersAndOwners(project));
            model.addAttribute("taskStatuses", taskStatusService.findAll());
            return "tasks/form";
        }

        Task task = taskService.findById(taskId);
        if (!task.getTaskStatus().getId().equals(form.getTaskStatusId()))
        {
            task.setStatusChangedAt(LocalDateTime.now());
        }
        task.setTaskStatus(taskStatusService.findById(form.getTaskStatusId()));
        task.setTitle(form.getTitle());
        task.setAssignee(userService.findById(form.getAssigneeId()));
        task.setReporter(userService.findById(form.getReporterId()));
        task.setPriority(form.getPriority());
        task.setStoryPoints(form.getStoryPoints());
        taskService.save(task);
        return "redirect:/projects/" + form.getProjectId() + "/tasks/" + taskId;
    }

    private List<User> mergeMembersAndOwners(Project project) {
        List<User> list = new ArrayList<>(project.getUsers());
        list.addAll(project.getOwners());
        return list;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{taskId}/delete")
    public String delete(@PathVariable Long projectId,
                         @PathVariable Long taskId) {
        taskService.delete(taskId);
        return "redirect:/projects/" + projectId + "/tasks";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/{taskId}")
    public String details(@PathVariable Long projectId,
                          @PathVariable Long taskId,
                          Model model) {
        Task task = taskService.findById(taskId);
        Project project = projectService.findById(projectId);

        // populate form for inline edit
        TaskForm form = TaskForm.fromEntity(task);
        form.setProjectId(projectId);
        model.addAttribute("taskForm", form);

        // dropdown data
        model.addAttribute("users", mergeMembersAndOwners(project));
        model.addAttribute("taskStatuses", taskStatusService.findAll());

        // other data
        model.addAttribute("project", project);
        model.addAttribute("task", task);
        model.addAttribute("comments", commentService.findByTask(task));
        model.addAttribute("newComment", new CommentForm());
        model.addAttribute("allLabels", labelService.findAll());
        List<Sprint> availableSprints = new ArrayList<>();
        for (Board board : project.getBoards()) {
            availableSprints.addAll(board.getSprints());
        }
        availableSprints.removeAll(task.getSprints());

        model.addAttribute("availableSprints", availableSprints);

        return "tasks/details";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{taskId}/comments")
    public String addComment(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @Valid @ModelAttribute("newComment") CommentForm form) {
        Task task = taskService.findById(taskId);
        Comment comment = new Comment();
        comment.setTask(task);
        comment.setText(form.getText());
        comment.setUser(userService.getCurrentUser());
        commentService.save(comment);
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{taskId}/sprints/add")
    public String addSprintToTask(@PathVariable Long projectId,
                                  @PathVariable Long taskId,
                                  @RequestParam Long sprintId) {
        Task task = taskService.findById(taskId);

        Sprint sprint = sprintService.findById(sprintId);

        if (sprint.getProject().equals(task.getProject())) {
            task.getSprints().add(sprint);
            taskService.save(task);
        }

        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{taskId}/sprints/{sprintId}/remove")
    public String removeSprintFromTask(@PathVariable Long projectId,
                                       @PathVariable Long taskId,
                                       @PathVariable Long sprintId) {
        Task task = taskService.findById(taskId);
        Sprint sprint = sprintService.findById(sprintId);
        if (task.getSprints().contains(sprint)) {
            task.getSprints().remove(sprint);
            taskService.save(task);
        }
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{taskId}/description")
    public String updateDescription(@PathVariable Long projectId,
                                    @PathVariable Long taskId,
                                    @RequestParam("description") String description) {
        Task task = taskService.findById(taskId);
        task.setDescription(description);
        taskService.save(task);
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
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
        task.setStoryPoints(form.getStoryPoints());

        return task;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody Map<String, String> payload
    ) {
        String statusIdStr = payload.get("taskStatusId");
        if (statusIdStr == null) {
            return ResponseEntity.badRequest().body("Missing taskStatusId");
        }

        Long statusId;
        try {
            statusId = Long.parseLong(statusIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid taskStatusId format");
        }

        Task task = taskService.findById(taskId);
        if (!task.getProject().getId().equals(projectId)) {
            return ResponseEntity.badRequest().build();
        }

        TaskStatus status = taskStatusService.findById(statusId);

        task.setTaskStatus(status);
        taskService.save(task);

        return ResponseEntity.ok().build();
    }

}


