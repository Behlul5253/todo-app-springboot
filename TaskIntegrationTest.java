package com.example.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = {"USER"})
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void postGetPutDeleteTask_ShouldPersistAndUpdateDatabaseState() throws Exception {
        // 1. POST - Create task and verify DB state
        Task newTask = new Task();
        newTask.setTitle("Integration Test Task");
        newTask.setDescription("Testing H2 persistence");
        newTask.setCompleted(false);

        MvcResult postResult = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Integration Test Task")))
                .andReturn();

        Task createdTask = objectMapper.readValue(
                postResult.getResponse().getContentAsString(), Task.class);
        Long taskId = createdTask.getId();

        assertEquals(1, taskRepository.count());
        assertTrue(taskRepository.findById(taskId).isPresent());

        // 2. GET - Retrieve tasks and verify response
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Integration Test Task")));

        // 3. PUT - Update task and verify DB state
        Task updatePayload = new Task();
        updatePayload.setTitle("Updated Integration Task");
        updatePayload.setDescription("Updated H2 record");
        updatePayload.setCompleted(true);

        mockMvc.perform(put("/api/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Integration Task")))
                .andExpect(jsonPath("$.completed", is(true)));

        Task fromDbAfterUpdate = taskRepository.findById(taskId).orElseThrow();
        assertEquals("Updated Integration Task", fromDbAfterUpdate.getTitle());
        assertTrue(fromDbAfterUpdate.isCompleted());

        // 4. DELETE - Remove task and verify DB is empty
        mockMvc.perform(delete("/api/tasks/" + taskId))
                .andExpect(status().isNoContent());

        assertEquals(0, taskRepository.count());
        assertFalse(taskRepository.findById(taskId).isPresent());
    }
}