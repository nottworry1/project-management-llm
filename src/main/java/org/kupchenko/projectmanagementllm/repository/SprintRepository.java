package org.kupchenko.projectmanagementllm.repository;

import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.kupchenko.projectmanagementllm.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProject(Project project);

    List<Sprint> findByBoard(Board board);
}

