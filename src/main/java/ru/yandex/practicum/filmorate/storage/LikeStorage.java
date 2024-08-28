package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {

    boolean addLike(Long filmId, Long userId);

    boolean likeRemove(Long filmId, Long userId);
}
