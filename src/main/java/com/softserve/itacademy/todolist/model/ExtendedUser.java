package com.softserve.itacademy.todolist.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter @Setter
public class ExtendedUser extends org.springframework.security.core.userdetails.User {
    private long id;
    public ExtendedUser(String username, String password,
                        Collection<? extends GrantedAuthority> authorities, long id) {
        super(username, password, authorities);
        this.id = id;
    }


}
