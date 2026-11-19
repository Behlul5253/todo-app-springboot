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
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Unit test description");
        sampleTask.setCompleted(false);
    }

    @Test
    void addTask_ShouldSaveAndReturnTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.addTask(sampleTask);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void addTask_ShouldThrowException_WhenTitleIsBlank() {
        Task invalidTask = new Task();
        invalidTask.setTitle("   ");

        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(invalidTask));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getAllTasks_ShouldReturnTasks() {
        Task secondTask = new Task();
        secondTask.setId(2L);
        secondTask.setTitle("Second Task");
        secondTask.setDescription("Second description");
        secondTask.setCompleted(true);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(sampleTask, secondTask));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
        assertEquals("Second Task", tasks.get(1).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoneExist() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<Task> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void updateTask_ShouldUpdateAndReturnTask_WhenTaskExists() {
        Task updatedInput = new Task();
        updatedInput.setTitle("Updated Task");
        updatedInput.setDescription("Updated description");
        updatedInput.setCompleted(true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = taskService.updateTask(1L, updatedInput);

        assertNotNull(updated);
        assertEquals("Updated Task", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertTrue(updated.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskNotFound() {
        Task updatedInput = new Task();
        updatedInput.setTitle("Missing Task");

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