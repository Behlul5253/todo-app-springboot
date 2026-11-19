package com.example.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task(1L, "Write JUnit 5 tests", "Cover CRUD operations", false);
    }

    @Test
    void addTask_ShouldSaveAndReturnTask_WhenValid() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task savedTask = taskService.addTask(sampleTask);

        assertNotNull(savedTask);
        assertEquals(1L, savedTask.getId());
        assertEquals("Write JUnit 5 tests", savedTask.getTitle());
        assertFalse(savedTask.isCompleted());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void addTask_ShouldThrowException_WhenTitleIsBlank() {
        Task invalidTask = new Task(null, "   ", "Invalid", false);

        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(invalidTask));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        Task secondTask = new Task(2L, "Push to GitHub", "Submit Day 15 task", true);
        when(taskRepository.findAll()).thenReturn(Arrays.asList(sampleTask, secondTask));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        assertEquals("Write JUnit 5 tests", tasks.get(0).getTitle());
        assertEquals("Push to GitHub", tasks.get(1).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasksExist() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<Task> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void updateTask_ShouldUpdateAndReturnTask_WhenTaskExists() {
        Task updatedInput = new Task(null, "Updated JUnit 5 test", "Updated description", true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1L, updatedInput);

        assertNotNull(result);
        assertEquals("Updated JUnit 5 test", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertTrue(result.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskNotFound() {
        Task updatedInput = new Task(null, "Missing task", "Desc", false);
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.updateTask(99L, updatedInput));
        verify(taskRepository, times(1)).findById(99L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        assertDoesNotThrow(() -> taskService.deleteTask(1L));

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(sampleTask);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, times(1)).findById(99L);
        verify(taskRepository, never()).delete(any(Task.class));
    }
}