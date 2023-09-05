package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.auth.UserDetailsImpl;
import com.softserve.itacademy.todolist.dto.UserRequest;
import com.softserve.itacademy.todolist.dto.UserResponseGetAll;
import com.softserve.itacademy.todolist.dto.UserResponseGetOne;
import com.softserve.itacademy.todolist.dto.UserTransformer;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.RoleService;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserTransformer userTransformer;

    @GetMapping
    List<UserResponseGetAll> getAllUsers() {
        return userService.getAll().stream()
                .map(UserResponseGetAll::new)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    UserResponseGetOne getUser(@PathVariable long id) {
        log.info("CONTROLLER GET /API/USERS/" + id);
        return userTransformer.fromEntityWithToDo(userService.readById(id));
    }

    @PostMapping("/")
    ResponseEntity<Void> createUser(@RequestBody UserRequest userRequest) {
        log.info("CONTROLLER POST /API/USERS/");
        User newUser = userTransformer.toEntity(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        newUser.setRole(roleService.findByName(userRequest.getRole().toUpperCase()));
        userService.create(newUser);
    return ResponseEntity.status(HttpStatus.CREATED)
            .header("Location", "/api/users/" + newUser.getId())
            .build();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userServiceImpl.readById(#id).getEmail() == authentication.name")
//    @PreAuthorize("hasRole('ADMIN') or @userAccess.isOwner(#id)")
//    this variant also works but demands one more class(namely UserAccess)

    ResponseEntity<Void>  updateUser(@PathVariable long id,
                                     @RequestBody UserRequest userRequest) {
        User fromDb = userService.readById(id);
                fromDb.setFirstName(userRequest.getFirstName());
                fromDb.setLastName(userRequest.getLastName());
                fromDb.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                fromDb.setRole(roleService.findByName(userRequest.getRole().toUpperCase()));
                userService.create(fromDb);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .header("Location", "/api/users/" + fromDb.getId())
                        .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable long id) {
        log.info("CONTROLLER DELETE /API/USERS/" + id);
        userService.delete(id);
    }
}
