package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserService implements UserStorage {
    private final UserStorage userStorage;
    private final FilmService filmService;


    @Override
    public User get(int userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь" + userId + "не найден.");
        }
        return user;
    }

    @Override
    public User save(User user) {
        return userStorage.save(user);
    }

    @Override
    public void update(User user) {
if(userStorage.get(user.getId()) == null) {
    throw new RuntimeException("Пользователь не найден!");
}
userStorage.update(user);
    }

    @Override
    public void addFriends(int userId, int friendId) {
final User user = userStorage.get(userId);
        if(userStorage.get(userId) == null) {
            throw new RuntimeException("Пользователь не найден" + userId);
        }
        final User friends = userStorage.get(friendId);
        if(userStorage.get(friendId) == null) {
            throw new RuntimeException("Друг не найден" + friendId;
        }
    }

    @Override
    public void deleteFriends(int userId, int friendId) {

    }
}
