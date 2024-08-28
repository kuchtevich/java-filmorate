package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.*;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;
    private final ValidateService validateService;

    @Autowired
    public UserController(UserService userService,ValidateService validateService) {
        this.userService = userService;
        this.validateService = validateService;
    }

    @PutMapping("{userId}/friends/{friendId}")
    public void addToFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        validateService.checkAlreadyFriend(userId, friendId);
        userService.addToFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        validateService.checkCorrectUser(userId);
        validateService.checkCorrectUser(friendId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{userId}/friends")
    public Collection<User> allUserFriends(@PathVariable Long userId) {
        validateService.checkCorrectUser(userId);
        return userService.allUserFriends(userId);
    }

    @GetMapping("{userId}/friends/common/{otherUserId}")
    public Collection<User> commonFriends(@PathVariable Long userId, @PathVariable Long otherUserId) {
        return userService.commonFriends(userId, otherUserId);
    }

    @PostMapping
    public User addUser(@RequestBody User newUser) {
        validateService.validateUserInput(newUser);
        return userService.addUser(newUser);
    }

    @PutMapping
    public User userUpdate(@RequestBody User newUser) {
        validateService.validUserUpdate(newUser);
        return userService.userUpdate(newUser);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping(value = {"{userId}"})
    @ResponseStatus(HttpStatus.OK)
    public void deleteUSer(@PathVariable("userId") Long id) {
        userService.userDelete(id);
    }
}

