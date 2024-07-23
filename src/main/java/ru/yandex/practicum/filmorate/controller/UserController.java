package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();


    //создание пользователя
    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Пришел Post запрос /films с телом {}", user);
        validUser(user);
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(Long.valueOf(user.getId()), user);
        log.info("Отправлен ответ Post / films с телом {}", user);
        return user;
    }

    //обновление пользователя
    @PutMapping
    public User updateUser(@RequestBody User updateUser) {
        log.info("Пришел Put запрос / films с телом {}", updateUser);
        if (updateUser.equals(users)) {
            log.info("Такой пользователь уже добавлен");
        }
        User firstUser = users.get(updateUser.getId());
        validUser(updateUser);
        firstUser.setEmail(updateUser.getEmail());
        firstUser.setLogin(updateUser.getLogin());
        firstUser.setName(updateUser.getName());
        firstUser.setBirthday(updateUser.getBirthday());
        if (firstUser.getName() == null) {
            firstUser.setName(updateUser.getLogin());
        }
        log.info("Отправлен ответ Put/ films с телом {}", firstUser);
        return firstUser;
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    //получение списка всех пользователей
    @GetMapping
    public Collection<User> getUser() {
        log.info("Получение списка всех пользователей");
        return users.values();
    }

    private void validUser(User user) {
//электронная почта не может быть пустой и должна содержать символ @;
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Error");
            throw new ValidationException("Email не должен иметь пробелов, а так же должен содержать символ @!");
        }
        //логин не может быть пустым и содержать пробелы;
        if (user.getLogin().isBlank()) {
            log.info("Error");
            throw new ValidationException("Login не должен быть пустым и содержать пробелы!");
        }
//имя для отображения может быть пустым — в таком случае будет использован логин;
        if (user.getName().isBlank()) {
            user.getName().equals(user.getLogin());
        }
//дата рождения не может быть в будущем.
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Error");
            throw new ValidationException("Дата рождения не может быть указана в будущем времени!");
        }
    }
}