package org.kupchenko.projectmanagementllm.repository;

import org.kupchenko.projectmanagementllm.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    Optional<TaskStatus> findByName(String name);
}
