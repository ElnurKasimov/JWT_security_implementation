package com.softserve.itacademy.todolist.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponseGetOne {
    Long id;
    String firstName;
    String lastName;
    String email;
    String role;
    List<ToDo> todos = new ArrayList<>();

  }
