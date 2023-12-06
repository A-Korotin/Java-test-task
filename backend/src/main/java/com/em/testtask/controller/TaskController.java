package com.em.testtask.controller;

import com.em.testtask.domain.Task;
import com.em.testtask.dto.domain.InputTaskDto;
import com.em.testtask.dto.domain.OutputTaskDto;
import com.em.testtask.dto.domain.TaskStatusDto;
import com.em.testtask.dto.error.ErrorDto;
import com.em.testtask.dto.query.TaskQueryDto;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.mapper.TaskMapper;
import com.em.testtask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
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
@Tag(name = "Tasks", description = "Create, retrieve, update and delete tasks")
public class TaskController {

    private final TaskService service;
    private final TaskMapper mapper;

    @GetMapping
    @Operation(summary = "Get all tasks",
            description = "Retrieve tasks based on query parameters",
            responses = {@ApiResponse(description = "Ok", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))})})
    @PageableAsQueryParam
    public Page<OutputTaskDto> getTasks(Pageable pageable, TaskQueryDto queryDto) {
        Task example = mapper.fromQueryDto(queryDto);

        return service.findMatching(pageable, Example.of(example)).map(mapper::toDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task",
            description = "Retrieve specific task by ID",
            responses = {@ApiResponse(description = "Ok", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid path variable", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "Not found (more details in response body)", responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorDto.class))})})
    public OutputTaskDto getTaskById(@PathVariable UUID id) {
        Task task = service.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id '%s' could not be found", id));

        return mapper.toDto(task);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create task",
            description = "Create task with data provided via request body",
            responses = {@ApiResponse(description = "Created", responseCode = "201", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid request body", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))})})
    public OutputTaskDto createTask(@Valid @RequestBody InputTaskDto dto, Principal currentUser) {
        Task task = mapper.fromDto(dto);
        task.setAuthorId(currentUser.getName());
        task = service.save(task);
        return mapper.toDto(task);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit task",
            description = "Update task with data provided via request body",
            responses = {@ApiResponse(description = "Ok", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid request body/path variables", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "No access", responseCode = "403", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "Not found (more details in response body)", responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorDto.class))})})
    @PreAuthorize("@taskSecurityService.userHasRightsToModify(#id, principal)")
    public OutputTaskDto editTask(@PathVariable UUID id, @Valid @RequestBody InputTaskDto dto) {
        Task task = mapper.fromDto(dto);
        task = service.updateById(id, task);

        return mapper.toDto(task);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update task status",
            description = "Edit task status. Provided status should be different from the existing one",
            responses = {@ApiResponse(description = "Ok", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid request body/path variables", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "No access", responseCode = "403", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "Not found (more details in response body)", responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorDto.class))})})
    @PreAuthorize("@taskSecurityService.userHasRightsToChangeStatus(#id, principal)")
    public OutputTaskDto editTaskStatus(@PathVariable UUID id, @Valid @RequestBody TaskStatusDto dto) {
        Task task = service.changeStatus(id, dto.status);

        return mapper.toDto(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task",
            description = "Delete task by id",
            responses = {@ApiResponse(description = "Deleted", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid path variable", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "No access", responseCode = "403", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "Not found (more details in response body)", responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorDto.class))})})
    @PreAuthorize("@taskSecurityService.userHasRightsToModify(#id, principal)")
    public void deleteTask(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
