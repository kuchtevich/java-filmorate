package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;
import java.util.Map;

public interface UserStorage {
    Map<Long, Set<User>> getFriends();

    User addUser(User user);

    User updateUser(User user);

    boolean deleteUser(Long id);

    Collection<User> allUsers();

    User userGet(Long id);
}
