package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.service.BoardService;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("projects/{projectId}/boards/{boardId}/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final BoardService boardService;
    private final SprintService sprintService;

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @ModelAttribute("board")
    public Board loadBoard(@PathVariable Long boardId,
                           @PathVariable Long projectId) {
        Board board = boardService.findById(boardId);
        if (!Objects.equals(board.getProject().getId(), projectId)) {
            throw new IllegalArgumentException("Project ID mismatch");
        }
        return board;
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping
    public String list(@PathVariable Long projectId, @ModelAttribute Board board, Model model) {
        List<Sprint> sprints = sprintService.findAllByBoard(board);
        model.addAttribute("sprints", sprints);
        return "sprints/index";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/create")
    public String createForm(@PathVariable Long projectId, @ModelAttribute Board board, Model model) {
        model.addAttribute("sprint", new Sprint());
        return "sprints/form";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping
    public String create(@PathVariable Long projectId,
                         @PathVariable Long boardId,
                         @Valid @ModelAttribute("sprint") Sprint sprint,
                         BindingResult br) {
        if (br.hasErrors()) {
            return "sprints/form";
        }
        Board board = boardService.findById(boardId);
        sprint.setBoard(board);
        sprint.setProject(board.getProject());
        sprintService.save(sprint);
        return "redirect:/projects/" + board.getProject().getId() + "/boards/" + board.getId() + "/sprints";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/{sprintId}/edit")
    public String editForm(@ModelAttribute Board board,
                           @PathVariable Long projectId,
                           @PathVariable Long sprintId,
                           Model model) {
        Sprint sprint = sprintService.findById(sprintId);
        model.addAttribute("sprint", sprint);
        return "sprints/form";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{sprintId}")
    public String update(@PathVariable Long projectId,
                         @PathVariable Long sprintId,
                         @Valid @ModelAttribute("sprint") Sprint sprint,
                         @PathVariable Long boardId,
                         BindingResult br) {
        if (br.hasErrors()) {
            return "sprints/form";
        }
        Board board = boardService.findById(boardId);
        Sprint existingSprint = sprintService.findById(sprintId);
        existingSprint.setName(sprint.getName());
        existingSprint.setStartDate(sprint.getStartDate());
        existingSprint.setEndDate(sprint.getEndDate());
        existingSprint.setDescription(sprint.getDescription());
        sprintService.save(sprint);
        return "redirect:/projects/" + board.getProject().getId() + "/boards/" + board.getId() + "/sprints";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/delete/{sprintId}")
    public String delete(@ModelAttribute Board board,
                         @PathVariable Long projectId,
                         @PathVariable Long sprintId) {
        sprintService.delete(sprintId);
        return "redirect:/projects/" + board.getProject().getId() + "/boards/" + board.getId() + "/sprints";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/{sprintId}")
    public String details(@ModelAttribute Board board,
                          @PathVariable Long projectId,
                          @PathVariable Long sprintId,
                          Model model) {
        Sprint sprint = sprintService.findById(sprintId);
        int totalStoryPoints = sprint.getTasks().stream()
                .filter(t -> t.getStoryPoints() != null)
                .mapToInt(Task::getStoryPoints)
                .sum();

        model.addAttribute("totalStoryPoints", totalStoryPoints);
        model.addAttribute("sprint", sprint);
        model.addAttribute("tasks", sprint.getTasks());
        return "sprints/details";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("{sprintId}/close")
    public String closeSprint(@PathVariable Long sprintId,
                              @PathVariable Long projectId,
                              @PathVariable Long boardId,
                              RedirectAttributes redirectAttributes) {
        boardService.closeSprint(sprintId);
        redirectAttributes.addFlashAttribute("success", "Sprint closed successfully!");
        return "redirect:/projects/" + projectId + "/boards/" + boardId + "/overview";
    }

}

