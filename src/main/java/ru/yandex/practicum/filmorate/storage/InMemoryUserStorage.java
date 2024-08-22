package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    @Getter
    private final Map<Long, Set<User>> friends = new HashMap<>();
    private long currentMaxId;

    @Override
    public User addUser(User newUser) {
        Map<Long, Set<User>> friends = new HashMap<>();
        Map<Long, User> users = new HashMap<>();
        validUser(newUser);
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        friends.put(newUser.getId(), new HashSet<>());
        return newUser;
    }

    @Override
    public Collection<User> allUsers() {
        return users.values();
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null || !users.containsKey(newUser.getId())) {
            log.error("Пользователь с id " + newUser.getId() + " не найден");
            throw new NotFoundException("id не найден");
        }
        User oldUser = users.get(newUser.getId());
        validUser(newUser);
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        if (oldUser.getName() == null) {
            oldUser.setName(oldUser.getLogin());
        }
        users.remove(newUser.getId());
        users.put(oldUser.getId(), oldUser);
        log.info("Отправлен ответ Put /users с телом {}", oldUser);
        return oldUser;
    }

    @Override
    public User userGet(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException(id +"id не найден");
        }
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException(id +"id не найден");
        }
        log.info("Пользователь удален");
        users.remove(id);
    }

    private void validUser(User user) {
//электронная почта не может быть пустой и должна содержать символ @;
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Error");
            throw new ValidationException("Email не должен иметь пробелов, а так же должен содержать символ @!");
        }
        //логин не может быть пустым и содержать пробелы;
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.info("Error");
            throw new ValidationException("Login не должен быть пустым и содержать пробелы!");
        }
//имя для отображения может быть пустым — в таком случае будет использован логин;
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
//дата рождения не может быть в будущем.
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Error");
            throw new ValidationException("Дата рождения не может быть указана в будущем времени!");
        }
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}