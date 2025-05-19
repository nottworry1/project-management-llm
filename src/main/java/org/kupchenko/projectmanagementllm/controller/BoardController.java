package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.model.TaskStatus;
import org.kupchenko.projectmanagementllm.service.BoardService;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.kupchenko.projectmanagementllm.service.TaskStatusService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects/{projectId}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final ProjectService projectService;
    private final BoardService boardService;
    private final SprintService sprintService;
    private final TaskStatusService taskStatusService;

    @ModelAttribute("taskStatuses")
    public List<TaskStatus> loadTaskStatuses() {
        return taskStatusService.findAll();
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping
    public String list(@PathVariable Long projectId, Model m) {
        Project project = projectService.findById(projectId);
        m.addAttribute("boards", boardService.findAllByProject(project));
        return "boards/index";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/create")
    public String createForm(@PathVariable Long projectId, Model m) {
        m.addAttribute("board", new Board());
        return "boards/form";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping
    public String create(@PathVariable Long projectId,
                         @Valid @ModelAttribute("board") Board board,
                         BindingResult br,
                         @RequestParam(value = "sprintId", required = false) Long sprintId) {
        if (br.hasErrors()) {
            return "boards/form";
        }
        if (sprintId != null) {
            Sprint sprint = sprintService.findById(sprintId);
            board.setCurrentSprint(sprint);
        } else {
            board.setCurrentSprint(null);
        }
        Project project = projectService.findById(projectId);
        board.setProject(project);
        boardService.save(board);
        return "redirect:/projects/" + project.getId() + "/boards";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/{boardId}/edit")
    public String editForm(@PathVariable Long projectId,
                           @PathVariable Long boardId, Model m) {
        Board b = boardService.findById(boardId);
        Project project = projectService.findById(projectId);
        m.addAttribute("board", b);
        m.addAttribute("sprints", sprintService.findAllByProject(project));
        return "boards/form";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/{boardId}")
    public String update(@PathVariable Long projectId,
                         @PathVariable Long boardId,
                         @RequestParam(value = "sprintId", required = false) Long sprintId,
                         @Valid @ModelAttribute("board") Board board,
                         BindingResult br, Model m) {
        Project project = projectService.findById(projectId);
        if (br.hasErrors()) {
            m.addAttribute("sprints", sprintService.findAllByProject(project));
            return "boards/form";
        }
        Board existingBoard = boardService.findById(boardId);
        existingBoard.setName(board.getName());
        boardService.save(existingBoard);

        if (sprintId != null) {
            boardService.linkSprint(existingBoard, sprintService.findById(sprintId));
        }
        return "redirect:/projects/" + project.getId() + "/boards";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @PostMapping("/delete/{boardId}")
    public String delete(@PathVariable Long projectId,
                         @PathVariable Long boardId) {
        boardService.delete(boardId);
        return "redirect:/projects/" + projectId + "/boards";
    }

    @SuppressWarnings("unused")
    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/{boardId}")
    public String details(@PathVariable Long projectId, @PathVariable Long boardId, Model m) {
        Board b = boardService.findById(boardId);
        m.addAttribute("board", b);
        return "boards/details";
    }

    @PreAuthorize("@securityEvaluator.canAccessProject(#projectId)")
    @GetMapping("/{boardId}/overview")
    public String boardOverview(
            @PathVariable Long projectId,
            @PathVariable Long boardId,
            Model model) {

        Board board = boardService.findById(boardId);
        model.addAttribute("board", board);

        Sprint currentSprint = board.getCurrentSprint();
        model.addAttribute("currentSprint", currentSprint);

        List<Sprint> sprints = sprintService.findAllByBoard(board);
        model.addAttribute("sprints", sprints);

        model.addAttribute("project", projectService.findById(projectId));

        return "boards/board-overview";
    }
}

