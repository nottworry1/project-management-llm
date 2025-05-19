package org.kupchenko.projectmanagementllm.controller;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Label;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.service.LabelService;
import org.kupchenko.projectmanagementllm.service.LabelSuggestService;
import org.kupchenko.projectmanagementllm.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/projects/{projectId}/tasks/{taskId}/labels")
@Controller
@RequiredArgsConstructor
public class TaskLabelController {

    private final TaskService taskService;
    private final LabelService labelService;
    private final LabelSuggestService labelSuggestService;

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping()
    public String saveLabel(@PathVariable Long projectId,
                            @PathVariable Long taskId,
                            @RequestParam String labelName) {
        Task task = taskService.findById(taskId);
        // Ensure task belongs to project
        if (!task.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Task does not belong to project");
        }
        // Find or create label
        Label label = labelService.findByNameOptional(labelName)
                .orElseGet(() -> labelService.save(new Label(labelName)));
        task.setLabel(label);
        taskService.save(task);
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/suggest")
    @ResponseBody
    public ResponseEntity<Map<String, String>> suggestLabel(@PathVariable Long taskId, @PathVariable Long projectId) {
        Task task = taskService.findById(taskId);

        if (!task.getProject().getId().equals(projectId)) {
            return ResponseEntity.notFound().build();
        }

        Label label = labelSuggestService.suggestLabel(task);

        Map<String, String> resp = new HashMap<>();
        resp.put("label", label.getName());
        return ResponseEntity.ok(resp);
    }
}

