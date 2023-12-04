package com.em.testtask.service;

import com.em.testtask.domain.Comment;

import java.util.UUID;

public interface CommentService extends PageableCrudService<Comment, UUID> {
}
