package com.em.testtask.mapper;

import com.em.testtask.domain.Comment;
import com.em.testtask.dto.InputCommentDto;
import com.em.testtask.dto.OutputCommentDto;
import com.em.testtask.mapper.config.MapConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapConfig.class)
public abstract class CommentMapper {
    public abstract Comment fromDto(InputCommentDto dto);
    public abstract OutputCommentDto toDto(Comment comment);
}
