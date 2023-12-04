package com.em.testtask.mapper;

import com.em.testtask.domain.Task;
import com.em.testtask.dto.InputTaskDto;
import com.em.testtask.dto.OutputTaskDto;
import com.em.testtask.mapper.config.MapConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapConfig.class, uses = {CommentMapper.class})
public abstract class TaskMapper {
    public abstract Task fromDto(InputTaskDto dto);
    public abstract OutputTaskDto toDto(Task task);
}
