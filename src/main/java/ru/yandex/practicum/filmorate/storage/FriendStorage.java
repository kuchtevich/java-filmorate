package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendStorage {

    boolean addFriend(Long userId, Long friendId);

    boolean friendRemove(Long userId, Long friendId);

    Collection<User> commonFriends(Long userIntersectionId, Long userId);

    Collection<User> userFriends(Long userId);
}

