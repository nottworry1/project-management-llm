package org.kupchenko.projectmanagementllm.repository;

import org.kupchenko.projectmanagementllm.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}

