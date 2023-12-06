package com.em.testtask.mvc;

import com.em.testtask.domain.Comment;
import com.em.testtask.domain.Task;
import com.em.testtask.dto.domain.InputCommentDto;
import com.em.testtask.service.CommentService;
import com.em.testtask.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.em.testtask.util.MockJwtAuthTools.testJwtAuth;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentMvcTest {
    @MockBean
    private TaskService taskService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private UUID taskId;
    private UUID commentId;

    @BeforeAll
    public void setup() {
        taskId = UUID.randomUUID();
        commentId = UUID.randomUUID();
    }

    @Test
    public void positiveCreateCommentTest() throws Exception {
        Task toReturn = new Task();
        given(taskService.findById(taskId)).willReturn(Optional.of(toReturn));

        InputCommentDto dto = new InputCommentDto();
        dto.comment = "Test";

        mvc.perform(post("/tasks/"+taskId+"/comments")
                .with(testJwtAuth("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void positiveUpdateCommentTest() throws Exception {
        Task toReturn = new Task();
        given(taskService.findById(taskId)).willReturn(Optional.of(toReturn));

        Comment commentToReturn = new Comment();
        commentToReturn.setTask(toReturn);
        commentToReturn.setAuthorId("author");
        given(commentService.findById(commentId)).willReturn(Optional.of(commentToReturn));

        InputCommentDto dto = new InputCommentDto();
        dto.comment = "Test";

        mvc.perform(put("/tasks/"+taskId+"/comments/"+commentId)
                .with(testJwtAuth("author"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void negativeUpdateCommentTest() throws Exception {
        Task toReturn = new Task();
        given(taskService.findById(taskId)).willReturn(Optional.of(toReturn));

        Comment commentToReturn = new Comment();
        commentToReturn.setTask(toReturn);
        commentToReturn.setAuthorId("author");
        given(commentService.findById(commentId)).willReturn(Optional.of(commentToReturn));

        InputCommentDto dto = new InputCommentDto();
        dto.comment = "Test";

        mvc.perform(put("/tasks/"+taskId+"/comments/"+commentId)
                        .with(testJwtAuth("not author"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void positiveDeleteCommentTest() throws Exception {
        Task toReturn = new Task();
        Comment commentToReturn = new Comment();
        commentToReturn.setTask(toReturn);
        commentToReturn.setAuthorId("author");
        commentToReturn.setId(commentId);
        toReturn.setComments(List.of(commentToReturn));
        given(commentService.findById(commentId)).willReturn(Optional.of(commentToReturn));
        given(taskService.findById(taskId)).willReturn(Optional.of(toReturn));

        InputCommentDto dto = new InputCommentDto();
        dto.comment = "Test";

        mvc.perform(delete("/tasks/"+taskId+"/comments/"+commentId)
                        .with(testJwtAuth("author"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void negativeDeleteCommentTest() throws Exception {
        Task toReturn = new Task();
        Comment commentToReturn = new Comment();
        commentToReturn.setTask(toReturn);
        commentToReturn.setAuthorId("author");
        toReturn.setComments(List.of(commentToReturn));
        given(commentService.findById(commentId)).willReturn(Optional.of(commentToReturn));
        given(taskService.findById(taskId)).willReturn(Optional.of(toReturn));

        InputCommentDto dto = new InputCommentDto();
        dto.comment = "Test";

        mvc.perform(delete("/tasks/"+taskId+"/comments/"+commentId)
                        .with(testJwtAuth("not author"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
