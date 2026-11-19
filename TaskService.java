package com.example.todo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task addTask(Task task) {
        if (task == null || task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title must not be empty");
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (updatedTask.getTitle() != null && !updatedTask.getTitle().isBlank()) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(existingTask);
    }
}