package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;


import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User addUser(User newUser) {
        log.info("Отправлен ответ Put / users с телом {}", newUser);
        return userStorage.addUser(newUser);
    }

    public Collection<User> allUsers() {
        log.info("Отправлен ответ GET /users");
        return userStorage.allUsers();
    }

    public User updateUser(User newUser) {
        log.info("Отправлен ответ Put / users с телом {}", newUser);
        return userStorage.updateUser(newUser);
    }

    public boolean deleteUser(Long id) {
        log.info("Пользователь c ID {} удален", id);
        return userStorage.deleteUser(id);
    }

    public boolean addFriend(Long userId, Long friendId) {
        log.info("Пользователи с ID {},{} теперь друзья", userId, friendId);
        return friendStorage.addFriend(userId, friendId);
    }

    public boolean friendRemove(Long userId, Long friendId) {
        log.info("Пользователи с ID {},{} теперь недрузья", userId, friendId);
        return friendStorage.friendRemove(userId, friendId);
    }


    public Collection<User> userFriends(Long userId) {
        log.info("Отправлен ответ GET /users/friends");
        return friendStorage.userFriends(userId);
    }


    public Collection<User> commonFriends(Long userIntersectionId, Long userId) {
        log.info("Отправлен ответ GET users/friends/common");
        return friendStorage.commonFriends(userIntersectionId, userId);
    }
}
