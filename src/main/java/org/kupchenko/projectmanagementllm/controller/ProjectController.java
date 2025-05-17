package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.kupchenko.projectmanagementllm.service.UserService;
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
    public String listProjects(Model model) {
        model.addAttribute("projects", projectService.findAll());
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

    @GetMapping("/{projectId}")
    public String showDetails(@PathVariable Long projectId, Model model) {
        model.addAttribute("project", projectService.findById(projectId));
        return "projects/details";
    }

    @GetMapping("/edit/{projectId}")
    public String showEditForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("project", projectService.findById(projectId));
        return "projects/edit";
    }

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

    @PostMapping("/delete/{projectId}")
    public String deleteProject(@PathVariable Long projectId) {
        projectService.deleteById(projectId);
        return "redirect:/projects";
    }
}

