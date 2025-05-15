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
    public String list(@ModelAttribute Project project, Model m) {
        m.addAttribute("boards", boardService.findAllByProject(project));
        return "boards/index";
    }

    @GetMapping("/create")
    public String createForm(@ModelAttribute Project project, Model m) {
        m.addAttribute("board", new Board());
        return "boards/form";
    }

    @PostMapping
    public String create(@ModelAttribute Project project,
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
        board.setProject(project);
        boardService.save(board);
        return "redirect:/projects/" + project.getId() + "/boards";
    }

    @GetMapping("/{boardId}/edit")
    public String editForm(@ModelAttribute Project project,
                           @PathVariable Long boardId, Model m) {
        Board b = boardService.findById(boardId);
        m.addAttribute("board", b);
        m.addAttribute("sprints", sprintService.findAllByProject(project));
        return "boards/form";
    }

    @PostMapping("/{boardId}")
    public String update(@ModelAttribute Project project,
                         @PathVariable Long boardId,
                         @RequestParam(value = "sprintId", required = false) Long sprintId,
                         @Valid @ModelAttribute("board") Board board,
                         BindingResult br, Model m) {
        if (br.hasErrors()) {
            m.addAttribute("sprints", sprintService.findAllByProject(project));
            return "boards/form";
        }
        board.setId(boardId);
        board.setProject(project);
        boardService.save(board);

        if (sprintId != null) {
            boardService.linkSprint(board, sprintService.findById(sprintId));
        }
        return "redirect:/projects/" + project.getId() + "/boards";
    }

    @PostMapping("/delete/{boardId}")
    public String delete(@ModelAttribute Project project,
                         @PathVariable Long boardId) {
        boardService.delete(boardId);
        return "redirect:/projects/" + project.getId() + "/boards";
    }

    @GetMapping("/{boardId}")
    public String details(@ModelAttribute Project project,
                          @PathVariable Long boardId, Model m) {
        Board b = boardService.findById(boardId);
        m.addAttribute("board", b);
        return "boards/details";
    }
}

