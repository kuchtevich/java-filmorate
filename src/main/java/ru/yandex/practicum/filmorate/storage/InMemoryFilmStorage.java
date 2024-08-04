package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    @Override
    public Film addFilm(Film film) {

    }

    @Override
    public Film updateFilm(Film film) {

    }

    @Override
    public Map<Long, Film> getFilms() {

    }

    @Override
    public Set<Long> addLike(Long id, Long idUser) {

    }

    @Override
    public Set<Long> deleteLike(Long id, Long idUser) {

    }

    @Override
    public List<Film> getPopular(Long count) {

    }

    @Override
    public Collection<Film> getAllFilms() {

    }
}
