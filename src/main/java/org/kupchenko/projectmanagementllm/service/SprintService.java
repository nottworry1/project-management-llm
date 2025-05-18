package org.kupchenko.projectmanagementllm.service;

import jakarta.transaction.Transactional;
import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;

import java.util.List;

public interface SprintService {
    Sprint findById(Long id);

    Sprint save(Sprint sprint);

    void delete(Long id);

    List<Sprint> findAllByProject(Project project);

    List<Sprint> findAllByBoard(Board board);

    @Transactional
    Sprint closeSprint(Long sprintId);
}
