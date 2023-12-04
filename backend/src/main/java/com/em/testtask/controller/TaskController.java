package com.em.testtask.controller;

import com.em.testtask.domain.Task;
import com.em.testtask.dto.domain.InputTaskDto;
import com.em.testtask.dto.domain.OutputTaskDto;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.mapper.TaskMapper;
import com.em.testtask.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;
    private final TaskMapper mapper;

    @GetMapping
    public Page<OutputTaskDto> getTasks(Pageable pageable) {
        return service.findAll(pageable).map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public OutputTaskDto getTaskById(@PathVariable UUID id) {
        Task task = service.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id '%s' could not be found", id));

        return mapper.toDto(task);
    }

    @PostMapping
    public OutputTaskDto createTask(@Valid @RequestBody InputTaskDto dto) {
        Task task = mapper.fromDto(dto);
        task = service.save(task);
        return mapper.toDto(task);
    }

    @PutMapping("/{id}")
    public OutputTaskDto editTask(@PathVariable UUID id, @Valid @RequestBody InputTaskDto dto) {
        Task task = mapper.fromDto(dto);
        task = service.updateById(id, task);

        return mapper.toDto(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
