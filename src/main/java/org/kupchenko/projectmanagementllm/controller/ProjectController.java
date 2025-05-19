package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.kupchenko.projectmanagementllm.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public String list() {
        return "projects/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "projects/create";
    }

    @PostMapping
    public String createProject(@Valid @ModelAttribute("project") Project project, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "projects/create";
        }
        project.addOwner(userService.getCurrentUser());
        projectService.save(project);
        return "redirect:/projects";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/{projectId}")
    public String showDetails(@PathVariable Long projectId, Model model) {
        model.addAttribute("project", projectService.findById(projectId));
        model.addAttribute("allUsers", userService.findAll());
        return "projects/details";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/edit/{projectId}")
    public String showEditForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("project", projectService.findById(projectId));
        return "projects/edit";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{projectId}")
    public String updateProject(@PathVariable Long projectId, @Valid @ModelAttribute("project") Project project, BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "projects/edit";
        }
        Project existingProject = projectService.findById(projectId);
        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        projectService.update(existingProject);
        return "redirect:/projects";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId) and @securityEvaluator.isProjectOwner(#projectId)")
    @PostMapping("/delete/{projectId}")
    public String deleteProject(@PathVariable Long projectId) {
        projectService.deleteById(projectId);
        return "redirect:/projects";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId) and @securityEvaluator.isProjectOwner(#projectId)")
    @PostMapping("/{projectId}/members/add")
    public String addMemberToProject(@PathVariable Long projectId,
                                     @RequestParam String username) {
        projectService.addMember(projectId, username);
        return "redirect:/projects/" + projectId;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId) and @securityEvaluator.isProjectOwner(#projectId)")
    @PostMapping("/{projectId}/owners/add")
    public String promoteToOwner(@PathVariable Long projectId,
                                 @RequestParam Long userId) {
        projectService.promoteToOwner(projectId, userId);
        return "redirect:/projects/" + projectId;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId) and @securityEvaluator.isProjectOwner(#projectId)")
    @PostMapping("/{projectId}/owners/remove")
    public String demoteOwner(@PathVariable Long projectId,
                              @RequestParam Long userId) {
        projectService.demoteOwner(projectId, userId);
        return "redirect:/projects/" + projectId;
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId) and @securityEvaluator.isProjectOwner(#projectId)")
    @PostMapping("/{projectId}/members/remove")
    public String removeMember(@PathVariable Long projectId, @RequestParam Long userId) {
        projectService.removeMember(projectId, userId);
        return "redirect:/projects/" + projectId;
    }

}

