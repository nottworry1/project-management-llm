package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;

import java.util.List;

public interface BoardService {
    List<Board> findAllByProject(Project project);

    Board findById(Long id);

    Board save(Board board);

    void linkSprint(Board board, Sprint sprint);

    void delete(Long id);
}
