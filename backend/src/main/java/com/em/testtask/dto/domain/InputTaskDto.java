package com.em.testtask.dto.domain;

import com.em.testtask.domain.TaskPriority;
import com.em.testtask.domain.TaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InputTaskDto {
    @NotNull
    @NotEmpty
    public String heading;

    public String description;

    @NotNull
    public TaskStatus status;

    @NotNull
    public TaskPriority priority;

    @NotNull
    @NotEmpty
    public String assigneeId;
}
