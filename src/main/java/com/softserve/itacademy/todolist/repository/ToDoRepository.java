package com.softserve.itacademy.todolist.repository;

import com.softserve.itacademy.todolist.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    @Query(value =
    "select id, created_at, title, owner_id from todos where owner_id = :userId ;", nativeQuery = true)
    List<ToDo> getByUserId(@Param("userId") long userId);
}
