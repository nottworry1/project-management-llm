package org.kupchenko.projectmanagementllm.repository;

import org.kupchenko.projectmanagementllm.model.Comment;
import org.kupchenko.projectmanagementllm.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskOrderByCreatedAtAsc(Task task);
}

