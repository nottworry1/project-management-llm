package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.repository.BoardRepository;
import org.kupchenko.projectmanagementllm.service.BoardService;
import org.kupchenko.projectmanagementllm.service.SprintAnalysisService;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final SprintService sprintService;
    private final SprintAnalysisService sprintAnalysisService;

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
        if (Boolean.TRUE.equals(sprint.getClosed())) {
            throw new IllegalArgumentException("Sprint can't be linked - it is already closed");
        }

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

    @Override
    @Transactional
    public Sprint closeSprint(Long sprintId) {
        Sprint sprint = sprintService.findById(sprintId);

        Board board = sprint.getBoard();

        if (!board.getCurrentSprint().getId().equals(sprintId) || Boolean.TRUE.equals(board.getCurrentSprint().getClosed())) {
            throw new IllegalArgumentException("Sprint can't be closed - it is either not current, or already closed");
        }


        sprint.setEndDate(LocalDateTime.now());
        sprint.setClosed(true);
        String summary = sprintAnalysisService.analyzeSprint(sprint);
        sprint.setSummary(summary);

        board.setCurrentSprint(null);
        save(board);

        return sprintService.save(sprint);
    }
}

