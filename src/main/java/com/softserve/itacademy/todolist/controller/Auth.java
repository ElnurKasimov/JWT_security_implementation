package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.auth.UserDetailsImpl;
import com.softserve.itacademy.todolist.auth.JwtUtils;
import com.softserve.itacademy.todolist.dto.UserRequest;
import com.softserve.itacademy.todolist.dto.UserTransformer;
import com.softserve.itacademy.todolist.exception.UsernameNotFoundException;
import com.softserve.itacademy.todolist.model.AuthResponse;
import com.softserve.itacademy.todolist.model.LoginRequest;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.RoleService;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class Auth {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

 @PostMapping("/login")
 public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        if(authentication==null) {
            throw new UsernameNotFoundException("There is no user with email " + loginRequest.getUsername());
        }
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(user.getUsername());
        AuthResponse authResponse = new AuthResponse(user.getUsername(), jwtToken);
        return ResponseEntity.ok().body(authResponse);
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
 }

    @PostMapping("/signup")
    ResponseEntity<Void> createUser(@RequestBody UserRequest userRequest) {
//        log.info("CONTROLLER POST /API/USERS/");
        User newUser = UserTransformer.toEntity(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        newUser.setRole(roleService.findByName(userRequest.getRole().toUpperCase()));
        userService.create(newUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/users/" + newUser.getId())
                .build();
    }

}

