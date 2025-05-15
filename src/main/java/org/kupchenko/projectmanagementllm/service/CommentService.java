package org.kupchenko.projectmanagementllm.service;

import org.kupchenko.projectmanagementllm.model.Comment;
import org.kupchenko.projectmanagementllm.model.Task;

import java.util.List;

public interface CommentService {
    List<Comment> findByTask(Task task);
    Comment save(Comment comment);
}
