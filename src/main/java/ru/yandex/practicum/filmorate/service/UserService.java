package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User newUser) {
        return userStorage.addUser(newUser);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }
    public User get(int userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь" + userId + "не найден.");
        }
        return user;
    }

    public User save(User user) {
        return userStorage.save(user);
    }

    public void update(User user) {
        if (userStorage.get(user.getId()) == null) {
            throw new RuntimeException("Пользователь не найден!");
        }
        userStorage.updateUser(user);
    }

    public void addFriends(int userId, int friendId) {
        final User user = userStorage.get(userId);
        if (userStorage.get(userId) == null) {
            throw new RuntimeException("Пользователь не найден" + userId);
        }
        final User friends = userStorage.get(friendId);
        if (userStorage.get(friendId) == null) {
            throw new RuntimeException("Друг не найден" + friendId;
        }
    }

    public void deleteFriends(int userId, int friendId) {

    }
}
