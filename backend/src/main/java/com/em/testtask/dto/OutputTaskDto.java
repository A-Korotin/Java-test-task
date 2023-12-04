package com.em.testtask.dto;

import com.em.testtask.domain.TaskPriority;
import com.em.testtask.domain.TaskStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OutputTaskDto {
    public UUID id;
    public String heading;
    public String description;
    public TaskStatus status;
    public TaskPriority priority;
    public String authorId;
    public String assigneeId;
    public List<OutputCommentDto> comments;
}
