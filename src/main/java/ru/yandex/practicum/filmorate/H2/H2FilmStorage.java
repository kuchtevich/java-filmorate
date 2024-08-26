package ru.yandex.practicum.filmorate.H2;


import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository("H2FilmStorage")
public class H2FilmStorage implements FilmStorage {
    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Film getFilm(Long id) {
        return null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return List.of();
    }

    @Override
    public void filmDelete(Long id) {

    }

    @Override
    public Map<Long, Set<Long>> getLikes() {
        return Map.of();
    }
}

