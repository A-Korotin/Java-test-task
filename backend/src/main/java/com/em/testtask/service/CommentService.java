package com.em.testtask.service;

import com.em.testtask.domain.Comment;
import com.em.testtask.domain.Task;

import java.util.UUID;

public interface CommentService extends CrudService<Comment, UUID> {
    Comment updateById(Task task, UUID commentId, Comment comment);
}
