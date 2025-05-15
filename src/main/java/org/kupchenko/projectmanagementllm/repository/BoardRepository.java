package org.kupchenko.projectmanagementllm.repository;

import org.kupchenko.projectmanagementllm.model.Board;
import org.kupchenko.projectmanagementllm.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByProject(Project project);
}
