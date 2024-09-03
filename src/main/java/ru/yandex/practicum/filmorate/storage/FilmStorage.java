package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film filmRequest);

    Film updateFilm(Film filmRequest);

    Collection<Film> allFilms();

    Collection<Film> popularFilms(Long count);

    Optional<Film> filmGet(Long id);

    boolean deleteFilm(Long id);
}
