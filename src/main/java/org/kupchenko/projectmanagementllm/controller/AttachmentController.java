package org.kupchenko.projectmanagementllm.controller;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Attachment;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.service.AttachmentService;
import org.kupchenko.projectmanagementllm.service.FileService;
import org.kupchenko.projectmanagementllm.service.TaskService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;

@Controller
@RequestMapping("/projects/{projectId}/tasks/{taskId}")
@RequiredArgsConstructor
public class AttachmentController {

    private final TaskService taskService;
    private final AttachmentService attachmentService;
    private final FileService fileService;

    @ModelAttribute("task")
    public Task loadTask(@PathVariable Long taskId) {
        return taskService.findById(taskId);
    }

    @PreAuthorize("@securityEvaluator.canAccessTask(#taskId)")
    @PostMapping("/attachments")
    public String uploadAttachment(@PathVariable Long projectId,
                                   @PathVariable Long taskId,
                                   @RequestParam("file") MultipartFile file,
                                   RedirectAttributes ra) {
        Task task = taskService.findById(taskId);
        try {
            attachmentService.save(file, task);
        } catch (IOException e) {
            ra.addFlashAttribute("error", "Upload failed");
        }
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @PreAuthorize("@securityEvaluator.canAccessTask(#taskId)")
    @PostMapping("/attachments/{attachmentId}/delete")
    public String deleteAttachment(@PathVariable Long projectId,
                                   @PathVariable Long taskId,
                                   @PathVariable Long attachmentId,
                                   RedirectAttributes ra) {
        Task task = taskService.findById(taskId);
        Attachment a = task.getAttachments().stream()
                .filter(att -> att.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such attachment"));
        try {
            attachmentService.delete(a);
        } catch (IOException e) {
            ra.addFlashAttribute("error", "Delete failed");
        }
        return "redirect:/projects/" + projectId + "/tasks/" + taskId;
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessTask(#taskId)")
    @GetMapping("/attachments/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable Long taskId,
                                              @PathVariable String filename) throws IOException {
        Path path = fileService.getFilePath(filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
