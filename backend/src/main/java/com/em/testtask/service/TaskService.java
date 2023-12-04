package com.em.testtask.service;

import com.em.testtask.domain.Task;
import com.em.testtask.domain.TaskStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TaskService extends CrudService<Task, UUID>, PageableService<Task> {
    Page<Task> findAllByAuthor(String authorId);
    Page<Task> findAllByAssignee(String assigneeId);
    Task changeStatus(UUID taskId, TaskStatus status);
}
