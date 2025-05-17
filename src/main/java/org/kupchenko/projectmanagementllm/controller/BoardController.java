package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.service.BoardService;
import org.kupchenko.projectmanagementllm.service.ProjectService;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/{projectId}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final ProjectService projectService;
    private final BoardService boardService;
    private final SprintService sprintService;

    @ModelAttribute("project")
    public Project loadProject(@PathVariable("projectId") Long projectId) {
        return projectService.findById(projectId);
    }

    @GetMapping
    public String list(@PathVariable Long projectId, Model m) {
        Project project = projectService.findById(projectId);
        m.addAttribute("boards", boardService.findAllByProject(project));
        return "boards/index";
    }

    @GetMapping("/create")
    public String createForm(Model m) {
        m.addAttribute("board", new Board());
        return "boards/form";
    }

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

    @GetMapping("/{boardId}/edit")
    public String editForm(@PathVariable Long projectId,
                           @PathVariable Long boardId, Model m) {
        Board b = boardService.findById(boardId);
        Project project = projectService.findById(projectId);
        m.addAttribute("board", b);
        m.addAttribute("sprints", sprintService.findAllByProject(project));
        return "boards/form";
    }

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

    @PostMapping("/delete/{boardId}")
    public String delete(@PathVariable Long projectId,
                         @PathVariable Long boardId) {
        boardService.delete(boardId);
        return "redirect:/projects/" + projectId + "/boards";
    }

    @GetMapping("/{boardId}")
    public String details(@PathVariable Long boardId, Model m) {
        Board b = boardService.findById(boardId);
        m.addAttribute("board", b);
        return "boards/details";
    }
}

