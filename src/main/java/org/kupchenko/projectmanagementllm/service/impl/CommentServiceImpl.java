package org.kupchenko.projectmanagementllm.service.impl;

import lombok.RequiredArgsConstructor;
import org.kupchenko.projectmanagementllm.model.Comment;
import org.kupchenko.projectmanagementllm.model.Task;
import org.kupchenko.projectmanagementllm.repository.CommentRepository;
import org.kupchenko.projectmanagementllm.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public List<Comment> findByTask(Task task) {
        return commentRepository.findByTaskOrderByCreatedAtAsc(task);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }
}

