package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void filmDelete(Long id) {
        filmStorage.filmDelete(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void filmLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        Set<Long> likes = filmStorage.getLikes().get(film.getId());
        if (likes.contains(userId)) {
            log.error("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Пользователь уже ставил лайк этому фильму");
        }
        User user = userStorage.userGet(userId);
        likes.add(user.getId());
        log.info("Фильму {} был поставлен лайк от пользователя {}", filmId, userId);
    }

    public void deleteLike(Long id, Long userId) {
        filmStorage.getFilm(id);
        Set<Long> likes = filmStorage.getLikes().get(id);
        User user = userStorage.userGet(userId);
        if (!likes.remove(user.getId())) {
            throw new ConditionsNotMetException("Пользователь " + userId + " Не ставил лайк этому фильму");
        }
        log.info("У фильма {} был удален лайк от пользователя {}", id, userId);
    }

    public List<Film> getPopular(Long count) {
        log.info("Было возращено {} популярных фильма", count);
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(film -> filmStorage.getLikes().get(film.getId()).size()))
                .limit(count)
                .collect(Collectors.toList()).reversed();
    }
}
