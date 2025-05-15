package org.kupchenko.projectmanagementllm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.service.BoardService;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("projects/{projectId}/boards/{boardId}/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final BoardService boardService;
    private final SprintService sprintService;

    @ModelAttribute("board")
    public Board loadBoard(@PathVariable Long boardId,
                           @PathVariable Long projectId) {
        Board board = boardService.findById(boardId);
        if (!Objects.equals(board.getProject().getId(), projectId))
        {
            throw new IllegalArgumentException("Project ID mismatch");
        }
        return board;
    }

    @GetMapping
    public String list(@ModelAttribute Board board, Model model) {
        List<Sprint> sprints = sprintService.findAllByBoard(board);
        model.addAttribute("sprints", sprints);
        return "sprints/index";
    }

    @GetMapping("/create")
    public String createForm(@ModelAttribute Board board, Model model) {
        model.addAttribute("sprint", new Sprint());
        return "sprints/form";
    }

    @PostMapping
    public String create(@PathVariable Long boardId,
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

    @GetMapping("/{sprintId}/edit")
    public String editForm(@ModelAttribute Board board,
                           @PathVariable Long sprintId,
                           Model model) {
        Sprint sprint = sprintService.findById(sprintId);
        model.addAttribute("sprint", sprint);
        return "sprints/form";
    }

    @PostMapping("/{sprintId}")
    public String update(@PathVariable Long sprintId,
                         @Valid @ModelAttribute("sprint") Sprint sprint,
                         @PathVariable Long boardId,
                         BindingResult br) {
        if (br.hasErrors()) {
            return "sprints/form";
        }
        Board board = boardService.findById(boardId);
        sprint.setId(sprintId);
        sprint.setBoard(board);
        sprint.setProject(board.getProject());
        sprintService.save(sprint);
        return "redirect:/projects/" + board.getProject().getId() + "/boards/" + board.getId() + "/sprints";
    }

    @PostMapping("/delete/{sprintId}")
    public String delete(@ModelAttribute Board board,
                         @PathVariable Long sprintId) {
        sprintService.delete(sprintId);
        return "redirect:/projects/" + board.getProject().getId() + "/boards/" + board.getId() + "/sprints";
    }

    @GetMapping("/{sprintId}")
    public String details(@ModelAttribute Board board,
                          @PathVariable Long sprintId,
                          Model model) {
        Sprint sprint = sprintService.findById(sprintId);
        model.addAttribute("sprint", sprint);
        model.addAttribute("tasks", sprint.getTasks());
        return "sprints/details";
    }
}

