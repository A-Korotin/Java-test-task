package com.em.testtask.controller;

import com.em.testtask.domain.Comment;
import com.em.testtask.domain.Task;
import com.em.testtask.dto.domain.InputCommentDto;
import com.em.testtask.dto.domain.OutputCommentDto;
import com.em.testtask.exception.NotFoundException;
import com.em.testtask.mapper.CommentMapper;
import com.em.testtask.service.CommentService;
import com.em.testtask.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final TaskService taskService;
    private final CommentMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
