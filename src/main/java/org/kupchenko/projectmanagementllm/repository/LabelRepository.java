package org.kupchenko.projectmanagementllm.repository;

import org.kupchenko.projectmanagementllm.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByNameIgnoreCase(String name);
}
