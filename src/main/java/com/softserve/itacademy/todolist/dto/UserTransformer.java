package com.softserve.itacademy.todolist.dto;

import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.repository.ToDoRepository;
import com.softserve.itacademy.todolist.service.ToDoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class UserTransformer {
    private final ToDoRepository toDoRepository;

    public User toEntity(UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        return user;
    }

    public  UserResponseGetAll fromEntity(User user) {
        return new UserResponseGetAll(user);
    }

    public UserResponseGetOne fromEntityWithToDo(User user) {
        UserResponseGetOne userResponseGetOne = new UserResponseGetOne();
        userResponseGetOne.setId(user.getId());
        userResponseGetOne.setFirstName(user.getFirstName());
        userResponseGetOne.setLastName(user.getLastName());
        userResponseGetOne.setEmail(user.getEmail());
        userResponseGetOne.setRole(user.getRole().getName());
        userResponseGetOne.setTodos(toDoRepository.getByUserId(user.getId()));
        return userResponseGetOne;
    }
}
