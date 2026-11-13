package com.example.todo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskController(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("newCategory", new Category());
        return "index";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String title, @RequestParam(required = false) Long categoryId) {
        Category category = categoryId != null ? categoryRepository.findById(categoryId).orElse(null) : null;
        taskRepository.save(new Task(title, category));
        return "redirect:/";
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String name) {
        categoryRepository.save(new Category(name));
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/";
    }
}