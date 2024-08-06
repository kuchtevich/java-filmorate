package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Comparator;


@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 0;

    private long nextId() {
        return ++currentId;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Set<Long> addLike(Long id, Long userId) {
        films.get(id).getLikes().remove(userId);
        return films.get(id).getLikes();
    }

    @Override
    public Set<Long> deleteLike(Long id, Long userId) {
        films.get(id).getLikes().remove(userId);
        return films.get(id).getLikes();
    }

    @Override
    public List<Film> getPopular(Long count) {
        return films.values().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());

    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}
