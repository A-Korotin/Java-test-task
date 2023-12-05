package com.em.testtask.service.impl;

import com.em.testtask.domain.Task;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskSecurityService {
    private final TaskService taskService;

    public boolean userHasRightsToModify(UUID taskId, Jwt currentUser) {
        Task task = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found"));
        String currentUserId = currentUser.getClaimAsString("sub");

        return task.getAuthorId().equals(currentUserId);
    }

    public boolean userHasRightsToChangeStatus(UUID taskId, Jwt currentUser) {
        Task task = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found"));
        String currentUserId = currentUser.getClaimAsString("sub");

        return task.getAuthorId().equals(currentUserId) ||
                task.getAssigneeId().equals(currentUserId);
    }
}
