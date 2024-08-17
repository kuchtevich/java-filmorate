package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
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


    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }
    public User get(Long userId) {
        final User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь" + userId + "не найден.");
        }
        return user;
    }


    public void updateUser(User user) {
        if (userStorage.getUser(user.getId()) == null) {
            throw new RuntimeException("Пользователь не найден!");
        }
        userStorage.updateUser(user);
    }

    public void addFriends(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (userStorage.getUser(userId) == null) {
            throw new RuntimeException("Пользователь не найден" + userId);
        }
        final User friends = userStorage.getUser(friendId);
        if (userStorage.getUser(friendId) == null) {
            throw new RuntimeException("Друг не найден" + friendId);
        }
    }

    public void deleteFriends(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (userStorage.getFriends().get(user.getId()).remove(friend) && userStorage.getFriends().get(friend.getId()).remove(user)) {
            log.info("Пользователь {} успешно удалил пользователя {}", userId, friendId);
        } else {
            log.warn("Пользователь {} не имел друга с ID {}", userId, friendId);
        }
    }
    public List<User> getAllFriends(Long userId) {
        User user = userStorage.getUser(userId);
        Set<User> userFriendsSet = userStorage.getFriends().get(user.getId());
        return new ArrayList<>(userFriendsSet);
    }
    public List<User> getUserFriends(Long userId, Long followerId) {
        User user = userStorage.getUser(userId);
        Set<User> userFriendsSet = userStorage.getFriends().get(user.getId());
        return new ArrayList<>(userFriendsSet);
    }
}
