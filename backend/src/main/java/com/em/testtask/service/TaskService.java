package com.em.testtask.service;

import com.em.testtask.domain.Task;
import com.em.testtask.domain.TaskStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService extends CrudService<Task, UUID>, PageableService<Task> {
    Task changeStatus(UUID taskId, TaskStatus status);
    Page<Task> findMatching(Pageable pageable, Example<Task> example);
}
