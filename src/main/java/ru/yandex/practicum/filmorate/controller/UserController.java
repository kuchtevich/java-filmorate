package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("{userId}/friends/{friendId}")
    public void addToFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addToFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{userId}/friends")
    public List<User> allUserFriends(@PathVariable Long userId) {
        return userService.allUserFriends(userId);
    }

    @GetMapping("{userId}/friends/common/{otherUserId}")
    public List<User> commonFriends(@PathVariable Long userId, @PathVariable Long otherUserId) {
        return userService.commonFriends(userId, otherUserId);
    }

    @PostMapping
    public User addUser(@RequestBody User newUser) {
        return userService.addUser(newUser);
    }

    @PutMapping
    public User userUpdate(@RequestBody User newUser) {
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