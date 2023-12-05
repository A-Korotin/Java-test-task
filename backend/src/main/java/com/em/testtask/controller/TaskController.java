package com.em.testtask.controller;

import com.em.testtask.domain.Task;
import com.em.testtask.dto.domain.InputTaskDto;
import com.em.testtask.dto.domain.OutputTaskDto;
import com.em.testtask.dto.domain.TaskStatusDto;
import com.em.testtask.dto.query.TaskQueryDto;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.mapper.TaskMapper;
import com.em.testtask.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;
    private final TaskMapper mapper;

    @GetMapping
    public Page<OutputTaskDto> getTasks(Pageable pageable, TaskQueryDto queryDto) {
        Task example = mapper.fromQueryDto(queryDto);

        return service.findMatching(pageable, Example.of(example)).map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public OutputTaskDto getTaskById(@PathVariable UUID id) {
        Task task = service.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id '%s' could not be found", id));

        return mapper.toDto(task);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutputTaskDto createTask(@Valid @RequestBody InputTaskDto dto, Principal currentUser) {
        Task task = mapper.fromDto(dto);
        task.setAuthorId(currentUser.getName());
        task = service.save(task);
        return mapper.toDto(task);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@taskSecurityService.userHasRightsToModify(#id, principal)")
    public OutputTaskDto editTask(@PathVariable UUID id, @Valid @RequestBody InputTaskDto dto) {
        Task task = mapper.fromDto(dto);
        task = service.updateById(id, task);

        return mapper.toDto(task);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@taskSecurityService.userHasRightsToChangeStatus(#id, principal)")
    public OutputTaskDto editTaskStatus(@PathVariable UUID id, @Valid @RequestBody TaskStatusDto dto) {
        Task task = service.changeStatus(id, dto.status);

        return mapper.toDto(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@taskSecurityService.userHasRightsToModify(#id, principal)")
    public void deleteTask(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
