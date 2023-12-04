package com.em.testtask.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    @Id
    private UUID id;

    private String heading;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    private String authorId;

    private String assigneeId;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;
}
