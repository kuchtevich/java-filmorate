package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public abstract class InMemoryUserStorage implements UserStorage {
private final Map<Long, User> users = new HashMap<>();
private long currentId = 0;

    private long nextId() {
        return ++currentId;
    }

@Override
public List<User> getAllFriends(Long userId) {
    return users.get(userId).getFriends().stream()
            .map(users::get)
            .collect(Collectors.toList());
}
    @Override
    public List<User> getUserFriends(Long userId, Long followerId) {
        Set<Long> user = users.get(userId).getFriends();
        Set<Long> userOther = users.get(followerId).getFriends();
        return user.stream()
                .filter(userOther::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }
    @Override
    public User addUser(User user) {
    user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

   @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
    @Override
    public Set<Long> addFriends(Long userId, Long friendId) {
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
        return users.get(userId).getFriends();
    }
    @Override
    public Set<Long> deleteFriends(Long userId, Long friendId) {
        users.get(userId).getFriends().remove(friendId);
        users.get(userId).getFriends().remove(userId);
        return users.get(userId).getFriends();
    }
    @Override
    public Collection<User> getAllUsers() {return users.values();}

}
