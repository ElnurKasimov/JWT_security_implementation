package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ToDo createToDo(@RequestBody ToDo todo) {
        return toDoService.create(todo);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ToDo getToDoById(@PathVariable long id) {
        return toDoService.readById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ToDo updateToDo(@PathVariable long id, @RequestBody ToDo todo) {
        // Make sure to set the id of the provided ToDo object to match the path variable 'id'
        todo.setId(id);
        return toDoService.update(todo);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/{id}")
    public void deleteToDo(@PathVariable long id) {
        toDoService.delete(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping
    public List<ToDo> getAllToDos() {
        return toDoService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/user/{userId}")
    public List<ToDo> getToDosByUserId(@PathVariable long userId) {
        return toDoService.getByUserId(userId);
    }
}
