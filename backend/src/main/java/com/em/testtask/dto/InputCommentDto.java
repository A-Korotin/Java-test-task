package com.em.testtask.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InputCommentDto {
    @NotNull
    @NotEmpty
    public String comment;
}
