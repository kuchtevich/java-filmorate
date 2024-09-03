package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {

    boolean filmLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);
}
