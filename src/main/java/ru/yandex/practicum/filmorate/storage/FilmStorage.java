package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long id);

    Collection<Film> getAllFilms();

    void filmDelete(Long id);

    Map<Long, Set<Long>> getLikes();
}
