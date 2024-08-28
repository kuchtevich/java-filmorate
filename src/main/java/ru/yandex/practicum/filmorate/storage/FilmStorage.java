package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film filmAdd(Film filmRequest);

    Film filmUpdate(Film filmRequest);

    Collection<Film> allFilms();

    Collection<Film> popularFilms(Long count);

    Optional<Film> getFilm(Long id);

    boolean filmDelete(Long id);
}
