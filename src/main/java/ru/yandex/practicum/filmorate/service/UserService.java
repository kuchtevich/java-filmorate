package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("H2UserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User newUser) {
        return userStorage.addUser(newUser);
    }

    public Collection<User> getAllUsers() {
        return userStorage.allUsers();
    }

    public User userUpdate(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public void userDelete(Long id) {
        userStorage.deleteUser(id);
    }

    public void addToFriend(Long userId, Long otherUserId) {
        User user = userStorage.userGet(userId);
        User otherUser = userStorage.userGet(otherUserId);
        if (userStorage.getFriends().get(user.getId()).contains(otherUser)) {
            log.error("Ошибка при добавлении друга: Пользователи {} и {} уже являются друзьями.", userId, otherUserId);
            throw new ValidationException("Пользователи уже в друзьях" + otherUserId);
        }
        userStorage.getFriends().get(user.getId()).add(userStorage.userGet(otherUserId));
        userStorage.getFriends().get(otherUser.getId()).add(userStorage.userGet(userId));
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, otherUserId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.userGet(userId);
        User friend = userStorage.userGet(friendId);
        if (userStorage.getFriends().get(user.getId()).remove(friend) && userStorage.getFriends().get(friend.getId()).remove(user)) {
            log.info("Пользователь {} успешно удалил пользователя {}", userId, friendId);
        } else {
            log.warn("Пользователь {} не имел друга с ID {}", userId, friendId);
        }
    }

    public List<User> allUserFriends(Long userId) {
        User user = userStorage.userGet(userId);
        Set<User> userFriendsSet = userStorage.getFriends().get(user.getId());
        return new ArrayList<>(userFriendsSet);
    }

    public List<User> commonFriends(Long userId, Long otherUserId) {
        List<User> userFriends = allUserFriends(userId);
        List<User> otherUserFriends = allUserFriends(otherUserId);
        List<User> commonFriends = new ArrayList<>(userFriends);
        commonFriends.retainAll(otherUserFriends);
        return commonFriends;
    }
}
