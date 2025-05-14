package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public String listProjects(Model model) {
        model.addAttribute("projects", projectService.findAll());
        return "projects/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        return fillModelAndReturnCreateForm(model);
    }

    private static String fillModelAndReturnCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "projects/create";
    }

    @PostMapping
    public String createProject(@Valid @ModelAttribute("project") Project project, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return fillModelAndReturnCreateForm(model);
        }
        projectService.save(project);
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectService.findById(id));
        return "projects/details";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return fillModelAndReturnEditForm(projectService.findById(id), model);
    }

    private String fillModelAndReturnEditForm(Project project, Model model) {
        model.addAttribute("project", project);
        return "projects/edit";
    }

    @PostMapping("/{id}")
    public String updateProject(@PathVariable Long id, @Valid @ModelAttribute("project") Project project, BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return fillModelAndReturnEditForm(project, model);
        }
        project.setId(id);
        projectService.update(project);
        return "redirect:/projects";
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
        return "redirect:/projects";
    }
}

