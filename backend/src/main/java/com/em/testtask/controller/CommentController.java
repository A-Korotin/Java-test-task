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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final TaskService taskService;
    private final CommentMapper mapper;

    @PostMapping
    public OutputCommentDto createComment(@PathVariable UUID taskId, @Valid @RequestBody InputCommentDto dto) {
        Comment comment = mapper.fromDto(dto);
        Task parentTask = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found", taskId));

        comment.setTask(parentTask);
        comment = commentService.save(comment);

        return mapper.toDto(comment);
    }

    @PutMapping("/{commentId}")
    public OutputCommentDto editComment(@PathVariable UUID taskId,
                                        @PathVariable UUID commentId,
                                        @Valid @RequestBody InputCommentDto dto) {
        Comment comment = mapper.fromDto(dto);
        Task parentTask = taskService.findById(taskId).orElseThrow(() ->
                new NotFoundException("Task with id '%s' could not be found", taskId));
        comment = commentService.updateById(parentTask, commentId, comment);

        return mapper.toDto(comment);
    }
}
