package com.em.testtask.service;

import com.em.testtask.domain.Task;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TaskService extends PageableCrudService<Task, UUID> {
    Page<Task> findAllByAuthor(String authorId);
    Page<Task> findAllByAssignee(String assigneeId);
}
