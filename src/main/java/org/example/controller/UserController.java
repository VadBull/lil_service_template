package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.entity.UserDto;
import org.example.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

    //TODO: 001 Rewrite according https://habr.com/ru/articles/675716/

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userService;

    //TODO: create pagination
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

    @PutMapping("/id/{userId}")
    public ResponseEntity<User> updateUserById(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.updateUserById(userId, userDto), HttpStatus.OK);
    }
    @PutMapping("/username/{username}")
    public ResponseEntity<User> updateUserByUserName(@PathVariable String username, @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.updateUserByUsername(username, userDto), HttpStatus.OK);
    }
}
