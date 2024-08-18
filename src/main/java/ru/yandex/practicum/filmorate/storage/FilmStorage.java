package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Map<Long, Film> getFilms();
    Film getFilm(Long id);

    Set<Long> addLike(Long id, Long userId);

    Set<Long> deleteLike(Long id, Long userId);

    List<Film> getPopular(Long count);
    Map<Long, Set<Long>> getLikes();

    Collection<Film> getAllFilms();
}
