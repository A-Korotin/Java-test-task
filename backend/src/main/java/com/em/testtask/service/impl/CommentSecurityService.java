package com.em.testtask.service.impl;

import com.em.testtask.domain.Comment;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentSecurityService {
    private final CommentService service;

    public boolean userHasRightsToModify(UUID commentId, Jwt currentUser) {
        Comment comment = service.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id '%s' could not be found", commentId));
        String currentUserId = currentUser.getClaimAsString("sub");

        return comment.getAuthorId().equals(currentUserId);
    }
}
