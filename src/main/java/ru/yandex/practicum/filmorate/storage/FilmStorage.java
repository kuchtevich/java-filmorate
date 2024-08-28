package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilm(Long id);

    Collection<Film> getAllFilms();

    boolean filmDelete(Long id);

    public Collection<Film> popularFilms(Long count);
}