package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

    public Film addFilm(Film film) {
        log.info("Отправлен ответ Put / films с телом {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Отправлен ответ Put / films с телом {}", film);
        return filmStorage.updateFilm(film);
    }

    public boolean filmDelete(final Long id) {
        log.info("Фильм удален c ID {} удален", id);
        return filmStorage.filmDelete(id);
    }

    public Collection<Film> getAllFilms() {
        log.info("Отправлен ответ GET /films");
        return filmStorage.getAllFilms();
    }

    public boolean filmLike(final Long filmId, final Long userId) {
        log.info("Фильму {} был поставлен лайк от пользователя {}", filmId, userId);
        return likeStorage.filmLike(filmId, userId);
    }

    public boolean deleteLike(final Long filmId, final Long userId) {
        log.info("У фильма {} был удален лайк от пользователя {}", filmId, userId);
        return likeStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getPopular(final Long count) {
        log.info("Было возращено {} популярных фильма", count);
        return filmStorage.popularFilms(count);
    }

    public Film getFilm(Long id) {
        Optional<Film> filmOptional = filmStorage.getFilm(id);
        if (filmOptional.isPresent()) {
            log.info("Отправлен ответ GET / films с телом {}", filmOptional.get());
            return filmOptional.get();
        } else {
            log.error("Такого фильма не существует");
            throw new ConditionsNotMetException("Фильма с ID " + id + " не существует");
        }
    }
}
