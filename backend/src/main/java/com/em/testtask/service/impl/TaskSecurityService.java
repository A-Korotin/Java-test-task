package com.em.testtask.service.impl;

import com.em.testtask.domain.Task;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskSecurityService {
    private final TaskService taskService;

    public boolean userHasRightsToModify(UUID taskId, UserDetails currentUser) {
        Task task = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found"));

        return task.getAuthorId().equals(currentUser.getUsername());
    }

    public boolean userHasRightsToChangeStatus(UUID taskId, UserDetails currentUser) {
        Task task = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found"));

        return task.getAuthorId().equals(currentUser.getUsername()) ||
                task.getAssigneeId().equals(currentUser.getUsername());
    }
}
