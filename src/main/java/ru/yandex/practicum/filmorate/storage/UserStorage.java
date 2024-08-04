package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {
    List<User> getAllFriends(Long userId);
    List<User> getUserFriends(Long userId, Long followerId);
    User addUser(User user);
    User updateUser(User user);
    Set<Long> addFriends(Long userId, Long friendId);
    Set<Long> deleteFriends(Long userId, Long friendId);
    Collection<User> getAllUsers();

}

