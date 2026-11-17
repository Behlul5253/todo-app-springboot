package com.example.todo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskApiController {
    private final TaskRepository taskRepository;

    public TaskApiController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasksJson() {
        return taskRepository.findAll();
    }
}