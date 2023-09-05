package com.softserve.itacademy.todolist.auth;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component()
public class UserAccess {

    @Autowired
    private UserService userService;

    public boolean isOwner(long id) {
        User user = userService.readById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return user.getEmail().equals(authentication.getName());
    }
}
