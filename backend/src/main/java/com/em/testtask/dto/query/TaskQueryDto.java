package com.em.testtask.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskQueryDto {
    public String authorId;
    public String assigneeId;
}
