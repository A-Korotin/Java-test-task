package com.em.testtask.controller;

import com.em.testtask.domain.Comment;
import com.em.testtask.domain.Task;
import com.em.testtask.dto.domain.InputCommentDto;
import com.em.testtask.dto.domain.OutputCommentDto;
import com.em.testtask.dto.error.ErrorDto;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.mapper.CommentMapper;
import com.em.testtask.service.CommentService;
import com.em.testtask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Tag(name = "Task comments", description = "Manage comments")
public class CommentController {
    private final CommentService commentService;
    private final TaskService taskService;
    private final CommentMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create comment",
            description = "Create comment and associate it to the task",
            responses = {@ApiResponse(description = "Created", responseCode = "201", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid request body/path variables", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "Not found (more details in response body)", responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorDto.class))})})
    public OutputCommentDto createComment(@PathVariable UUID taskId,
                                          @Valid @RequestBody InputCommentDto dto,
                                          Principal currentUser) {
        Comment comment = mapper.fromDto(dto);
        Task parentTask = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found", taskId));

        comment.setTask(parentTask);
        comment.setAuthorId(currentUser.getName());
        comment = commentService.save(comment);

        return mapper.toDto(comment);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update comment",
            description = "Update comment contents",
            responses = {@ApiResponse(description = "Ok", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid request body/path variables", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "No access", responseCode = "403", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "Not found (more details in response body)", responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorDto.class))})})
    @PreAuthorize("@commentSecurityService.userHasRightsToModify(#commentId, principal)")
    public OutputCommentDto editComment(@PathVariable UUID taskId,
                                        @PathVariable UUID commentId,
                                        @Valid @RequestBody InputCommentDto dto) {
        Comment comment = mapper.fromDto(dto);
        Task parentTask = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found", taskId));
        comment = commentService.updateById(parentTask, commentId, comment);

        return mapper.toDto(comment);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete comment",
            description = "Delete comment",
            responses = {@ApiResponse(description = "Deleted", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Invalid path variables", responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDto.class))}),
                    @ApiResponse(description = "Missing/invalid access token", responseCode = "401", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "No access", responseCode = "403", content = {@Content(schema = @Schema(implementation = Void.class))}),
                    @ApiResponse(description = "Not found (more details in response body)", responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorDto.class))})})
    @PreAuthorize("@commentSecurityService.userHasRightsToModify(#commentId, principal)")
    public void deleteById(@PathVariable UUID taskId, @PathVariable UUID commentId) {
        Task parentTask = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found", taskId));

        if (parentTask.getComments().stream().noneMatch((c) -> c.getId().equals(commentId))) {
            throw new NotFoundException("Task with id '%s' does not contain comment with id '%s'", taskId, commentId);
        }

        commentService.deleteById(commentId);
    }
}
