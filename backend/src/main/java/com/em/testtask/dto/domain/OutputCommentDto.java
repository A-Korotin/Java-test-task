package com.em.testtask.dto.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class OutputCommentDto {
    public UUID id;
    public String comment;
    public String authorId;
}
