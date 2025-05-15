package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.repository.BoardRepository;
import org.kupchenko.projectmanagementllm.service.BoardService;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final SprintService sprintService;

    @Override
    public List<Board> findAllByProject(Project project) {
        return boardRepository.findByProject(project);
    }

    @Override
    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board " + id));
    }

    @Override
    public Board save(Board board) {
        return boardRepository.save(board);
    }

    @Override
    @Transactional
    public void linkSprint(Board board, Sprint sprint) {
        board.addSprint(sprint);
        sprint.setBoard(board);
        sprintService.save(sprint);

        board.setCurrentSprint(sprint);
        boardRepository.save(board);
    }

    @Override
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }
}

