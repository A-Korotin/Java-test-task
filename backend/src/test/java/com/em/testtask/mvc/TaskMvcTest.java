package com.em.testtask.mvc;

import com.em.testtask.domain.Task;
import com.em.testtask.domain.TaskPriority;
import com.em.testtask.domain.TaskStatus;
import com.em.testtask.dto.domain.InputTaskDto;
import com.em.testtask.dto.domain.TaskStatusDto;
import com.em.testtask.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.em.testtask.util.MockJwtAuthTools.testJwtAuth;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskMvcTest {
    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private UUID validId = UUID.randomUUID();


    @Test
    public void positiveRetrieveTaskTest() throws Exception {
        Task toReturn = new Task();
        toReturn.setId(validId);
        toReturn.setAuthorId("TEST");
        given(taskService.findById(validId)).willReturn(Optional.of(toReturn));

        mvc.perform(get("/tasks/" + validId)
                .with(testJwtAuth("TEST")))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.authorId").value("TEST"),
                        jsonPath("$.id").value(validId.toString()));
    }

    @Test
    public void negativeRetrieveTaskTest() throws Exception {
        given(taskService.findById(validId)).willReturn(Optional.empty());

        mvc.perform(get("/tasks/" + validId)
                .with(testJwtAuth("TEST")))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void negativeEditTaskTest() throws Exception {
        Task toReturn = new Task();
        toReturn.setId(validId);
        toReturn.setAuthorId("Some other id");

        given(taskService.findById(validId)).willReturn(Optional.of(toReturn));

        InputTaskDto dto = InputTaskDto.builder()
                .heading("h")
                .description("d")
                .assigneeId("a")
                .status(TaskStatus.FINISHED)
                .priority(TaskPriority.HIGH)
                .build();

        mvc.perform(put("/tasks/" + validId)
                .with(testJwtAuth("TEST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void positiveChangeStatusTest() throws Exception{
        Task toReturn = new Task();
        toReturn.setId(validId);
        toReturn.setStatus(TaskStatus.WAITING);
        toReturn.setAuthorId("Some other id");
        toReturn.setAssigneeId("assignee");

        given(taskService.findById(validId)).willReturn(Optional.of(toReturn));

        TaskStatusDto dto = new TaskStatusDto(TaskStatus.IN_PROGRESS);

        mvc.perform(patch("/tasks/" + validId)
                .with(testJwtAuth("assignee"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void negativeChangeStatusTest() throws Exception {
        Task toReturn = new Task();
        toReturn.setId(validId);
        toReturn.setStatus(TaskStatus.WAITING);
        toReturn.setAuthorId("Some other id");
        toReturn.setAssigneeId("assignee");

        given(taskService.findById(validId)).willReturn(Optional.of(toReturn));

        TaskStatusDto dto = new TaskStatusDto(TaskStatus.IN_PROGRESS);

        mvc.perform(patch("/tasks/" + validId)
                        .with(testJwtAuth("stranger id"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void negativeDeleteTest() throws Exception {
        Task toReturn = new Task();
        toReturn.setAuthorId("author");

        given(taskService.findById(validId)).willReturn(Optional.of(toReturn));

        mvc.perform(delete("/tasks/"+validId)
                .with(testJwtAuth("not author")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void positiveDeleteTest() throws Exception {
        Task toReturn = new Task();
        toReturn.setAuthorId("author");

        given(taskService.findById(validId)).willReturn(Optional.of(toReturn));

        mvc.perform(delete("/tasks/"+validId)
                        .with(testJwtAuth("author")))
                .andExpect(status().isNoContent());
    }
}
