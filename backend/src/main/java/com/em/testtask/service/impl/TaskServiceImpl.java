package com.em.testtask.service.impl;

import com.em.testtask.domain.Task;
import com.em.testtask.domain.TaskStatus;
import com.em.testtask.exception.BadRequestException;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.repository.TaskRepository;
import com.em.testtask.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    @Override
    public Optional<Task> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Task save(Task entity) {
        return repository.save(entity);
    }

    @Override
    public Task updateById(UUID id, Task entity) {
        entity.setId(id);
        return save(entity);
    }

    @Override
    public void deleteById(UUID id) {
        if (!existsById(id)) {
            throw new NotFoundException("Task with id '%s' does not exist", id);
        }

        repository.deleteById(id);
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Task changeStatus(UUID taskId, TaskStatus status) {
        Task task = findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found", taskId));

        if (task.getStatus().equals(status)) {
            throw new BadRequestException("Task status is already '%s'", status);
        }

        task.setStatus(status);

        return repository.save(task);
    }

    @Override
    public Page<Task> findMatching(Pageable pageable, Example<Task> example) {
        return repository.findAll(example, pageable);
    }
}
