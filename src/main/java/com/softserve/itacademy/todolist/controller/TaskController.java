package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.TaskDto;
import com.softserve.itacademy.todolist.dto.TaskResponseDto;
import com.softserve.itacademy.todolist.dto.TaskTransformer;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users/{user_id}/todos/{todo_id}/tasks")
public class TaskController {
    Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;


    @Autowired
    public TaskController(TaskService taskService, ToDoService toDoService, StateService stateService) {
        this.taskService = taskService;
        this.toDoService = toDoService;
        this.stateService = stateService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@PathVariable(value = "todo_id") Long toDoId, @RequestBody TaskDto taskDto) {
        ToDo toDo = toDoService.readById(toDoId);
        logger.info("ToDo with id " + toDoId + " is present");

        Task task = TaskTransformer.convertToEntity(taskDto, toDo, stateService.getByName("NEW"));
        Task newTask = taskService.create(task);
        logger.info("Task created with ID: " + newTask.getId() + " Name: " + newTask.getName() + " and Priority: " + newTask.getPriority());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "api/tasks/" + newTask.getId())
                .body(new TaskResponse(newTask));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<TaskResponse> read(@PathVariable(value = "todo_id") Long toDoId, @PathVariable(value = "id") Long id) {
        ToDo toDo = toDoService.readById(toDoId);
        Task task = taskService.readById(id);
        if(!task.getTodo().equals(toDo))
            throw new AccessDeniedException("Access denied reading task");

        logger.info("Task with id " + task.getId() + " was found");

        return ResponseEntity.status(HttpStatus.OK)
                .body(new TaskResponse(task));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<TaskResponse> update(@PathVariable(value = "todo_id") Long todoId, @PathVariable(value = "id") Long id,
                                                  @RequestBody TaskDto taskDto) {
        if (taskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(taskDto.getTodoId() != todoId) {
            throw new AccessDeniedException("Access denied updating task");
        }
        logger.info("TaskDto with Name: " + taskDto.getName() + ", Priority: " + taskDto.getPriority() +
                ", ToDoId" + taskDto.getTodoId() + " and StateID: " + taskDto.getStateId());

        Task task = TaskTransformer.convertToEntity(taskDto,
                toDoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId())
        );
        task.setId(id);
        taskService.update(task);
        logger.info("Task with ID " + task.getId() + " was updated successfully");

        return ResponseEntity.status(HttpStatus.OK)
                .body(new TaskResponse(task));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Object> delete(@PathVariable(value = "user_id") Long userId, @PathVariable(value = "id") Long id) {
        if(!userId.equals(taskService.readById(id).getTodo().getOwner().getId())) {
            throw new AccessDeniedException("Access denied deleting task");
        }
        taskService.delete(id);
        logger.info("Task with ID " + id + " was deleted successfully");

        return ResponseEntity.status(HttpStatus.OK).body("Task with ID " + id + " was deleted successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TaskResponse>> getAll(@PathVariable(value = "todo_id") Long todoId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(toDoService.readById(todoId).getTasks().stream()
                        .map(TaskResponse::new)
                        .collect(Collectors.toList())
                );
    }
}
