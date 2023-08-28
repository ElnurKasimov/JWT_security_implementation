package com.softserve.itacademy.todolist.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    @Value("${myjwttoken.app.jwtSecret}")
    private String jwtSecret;

    @Value("${myjwttoken.app.jwtExpirationMs}")
    private int jwtExpirationMs;

}
