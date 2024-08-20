package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Getter
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private long currentId = 0;

    private long nextId() {
        return ++currentId;
    }

    @Override
    public Film addFilm(Film filmRequest) {
        validFilm(filmRequest);
        Film film = setFilm(filmRequest);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null || films.get(newFilm.getId()) == null) {
            log.error("Фильм с id " + newFilm.getId() + " не найден");
            throw new ConditionsNotMetException("id не найден");
        }
        Film oldFilm = setFilm(newFilm);
        validFilm(newFilm);
        films.remove(newFilm.getId());
        films.put(oldFilm.getId(), oldFilm);
        log.info("Отправлен ответ Put / films с телом {}", oldFilm);
        return oldFilm;
    }


    @Override
    public Film getFilm(Long id) {
        if (films.get(id) == null) {
            throw new ConditionsNotMetException("id не найден");
        }
        return films.get(id);
    }

    @Override
    public void filmDelete(Long id) {
        if (films.get(id) == null) {
            throw new ConditionsNotMetException("id не найден");
        }
        log.info("Фильм удален");
        films.remove(id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private Film setFilm(Film filmRequest) {
        Film film = new Film();
        if (films.get(filmRequest.getId()) == null) {
            film.setId(getNextId());
        } else {
            film.setId(filmRequest.getId());
        }
        film.setName(filmRequest.getName());
        film.setDescription(filmRequest.getDescription());
        film.setReleaseDate(filmRequest.getReleaseDate());
        film.setDuration(filmRequest.getDuration());
        return film;
    }

    private void validFilm(Film film) {
        //название не может быть пустым;
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Error");
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        //максимальная длина описания — 200 символов;
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.info("Error");
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        //дата релиза — не раньше 28 декабря 1895 года;
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 20))) {
            log.info("Error");
            throw new ValidationException("Дата не может быть раньше 1895г.!");
        }
        //продолжительность фильма должна быть положительным числом.
        if (film.getDescription() == null || film.getDuration() <= 0) {
            log.info("Error");
            throw new ValidationException("Продолжительность фильма не может быть меньше 0!");
        }
    }

    private long getNextId() {
        return ++currentId;
    }

}
