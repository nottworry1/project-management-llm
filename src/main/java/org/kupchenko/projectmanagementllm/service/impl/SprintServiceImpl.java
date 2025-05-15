package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.kupchenko.projectmanagementllm.repository.SprintRepository;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;

    @Override
    public Sprint findById(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found: " + id));
    }

    @Override
    public Sprint save(Sprint sprint) {
        return sprintRepository.save(sprint);
    }

    @Override
    public void delete(Long id) {
        sprintRepository.deleteById(id);
    }

    @Override
    public List<Sprint> findAllByProject(Project project) {
        return sprintRepository.findByProject(project);
    }

    @Override
    public List<Sprint> findAllByBoard(Board board) {
        return sprintRepository.findByBoard(board);
    }
}

