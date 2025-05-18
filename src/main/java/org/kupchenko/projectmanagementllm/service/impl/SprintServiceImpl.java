package org.kupchenko.projectmanagementllm.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.*;
import org.kupchenko.projectmanagementllm.repository.SprintRepository;
import org.kupchenko.projectmanagementllm.service.SprintAnalysisService;
import org.kupchenko.projectmanagementllm.service.SprintService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;
    private final SprintAnalysisService sprintAnalysisService;

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

    @Override
    @Transactional
    public Sprint closeSprint(Long sprintId) {
        Sprint sprint = findById(sprintId);
        String summary = sprintAnalysisService.analyzeSprint(sprint);

        sprint.setEndDate(LocalDateTime.now());
        sprint.setClosed(true);
        sprint.setSummary(summary);

        return sprintRepository.save(sprint);
    }
}

