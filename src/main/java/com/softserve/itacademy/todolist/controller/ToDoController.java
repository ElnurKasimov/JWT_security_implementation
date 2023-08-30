package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.CollaboratorResponse;
import com.softserve.itacademy.todolist.dto.ToDoResponse;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.ToDoService;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ToDoController {
    @Autowired
    private ToDoService toDoService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/api/users/{user_id}/todos")
    @ResponseStatus(HttpStatus.CREATED)
    ToDoResponse createToDo(@PathVariable("user_id") Long userId, @RequestBody ToDo toDoCreate) {
        ToDo toDo = new ToDo();
        toDo.setOwner(userService.readById(userId));
        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setTitle(toDoCreate.getTitle());
        toDo.setTasks(new ArrayList<>());
        toDo.setCollaborators(new ArrayList<>());
        log.info("Created new ToDo with title " + toDo.getTitle() + " and ID " + userId);
        return new ToDoResponse(toDoService.create(toDo));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/api/users/todos/{todo_id}")
    ToDoResponse readToDo(@PathVariable("todo_id") Long toDoId) {
        log.info("View ToDo with ID " + toDoId);
        return new ToDoResponse(toDoService.readById(toDoId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/api/users/todos/{todo_id}")
    ToDoResponse updateToDo(@PathVariable("todo_id") Long toDoId, @RequestBody ToDo toDoUpdate){
        ToDo toDo = toDoService.readById(toDoId);
        toDo.setTitle(toDoUpdate.getTitle());
        log.info("Edited ToDo with ID " + toDoId);
        return new ToDoResponse(toDoService.update(toDo));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/api/users/todos/{todo_id}")
    ToDoResponse deleteToDo(@PathVariable("todo_id") Long toDoId){
        ToDoResponse toDoResponse = new ToDoResponse(toDoService.readById(toDoId));
        toDoService.delete(toDoId);
        log.info("Deleted ToDo with ID " + toDoId);
        return toDoResponse;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/api/users/todos")
    List<ToDoResponse> getAllToDos(){
        log.info("Got the list of all ToDos");
        return toDoService.getAll().stream()
                .map(ToDoResponse::new)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/api/users/todos/{todo_id}/collaborators")
    List<CollaboratorResponse> getCollaborators(@PathVariable("todo_id") Long toDoId){
        log.info(String.format("Got hte list of collaborators of the ToDo %s",
                toDoService.readById(toDoId).getTitle()));
        return toDoService.readById(toDoId).getCollaborators().stream()
                .map(c -> new CollaboratorResponse(c, toDoService.readById(toDoId)))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/api/users/todos/{todo_id}/collaborators/add/{collaborator_id}")
    List<CollaboratorResponse> addCollaborator(@PathVariable("todo_id") Long toDoId,
                                               @PathVariable("collaborator_id") Long collaboratorId){
        ToDo toDo = toDoService.readById(toDoId);
        List<User> collaborators = toDo.getCollaborators();
        collaborators.add(userService.readById(collaboratorId));
        toDo.setCollaborators(collaborators);
        toDoService.update(toDo);
        log.info(String.format("Added new collaborator %s %s to the ToDo %s",
                userService.readById(collaboratorId).getFirstName(), userService.readById(collaboratorId).getLastName(),
                toDo.getTitle()));
        return toDoService.readById(toDoId).getCollaborators().stream()
                .map(c -> new CollaboratorResponse(c, toDoService.readById(toDoId)))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/api/users/todos/{todo_id}/collaborators/remove/{collaborator_id}")
    List<CollaboratorResponse> deleteCollaborator(@PathVariable("todo_id") Long toDoId,
                                                  @PathVariable("collaborator_id") Long collaboratorId){
        ToDo toDo = toDoService.readById(toDoId);
        List<User> collaborators = toDo.getCollaborators();
        collaborators.remove(userService.readById(collaboratorId));
        toDo.setCollaborators(collaborators);
        toDoService.update(toDo);
        log.info(String.format("Removed collaborator %s %s from the ToDo %s",
                userService.readById(collaboratorId).getFirstName(), userService.readById(collaboratorId).getLastName(),
                toDo.getTitle()));
        return toDoService.readById(toDoId).getCollaborators().stream()
                .map(c -> new CollaboratorResponse(c, toDoService.readById(toDoId)))
                .collect(Collectors.toList());
    }
}
