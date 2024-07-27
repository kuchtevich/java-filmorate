package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    //добавление фильма
    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Пришел Post запрос /films с телом {}", film);
        validFilm(film);
        //нет обновления хранилища
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Отправлен ответ Post / films с телом {}", film);
        return film;
    }

    //обновление фильма
    @PutMapping
    public Film getUpdatefilm(@RequestBody Film updateFilm) {
        log.info("Пришел Put запрос / films с телом {}", updateFilm);
        if (updateFilm.getId() == null || films.get(updateFilm.getId()) == null) {
            log.error("Фильм с id " + updateFilm.getId() + " не найден");
            throw new ValidationException("id не найден");
        }
        Film secondFilm = setFilm(updateFilm);
        validFilm(secondFilm);
        log.info("Поступил Put запрос /films с телом {}", updateFilm);
        films.put(updateFilm.getId(), updateFilm);
        log.info("Отправлен ответ Pге / films с телом {}", secondFilm);
        return secondFilm;
    }


    private Film setFilm(Film updateFilm) {
        Film film = new Film();
        if (films.get(updateFilm.getId()) == null) {
            film.setId(getNextId());
        }
        if (films.get(updateFilm.getId()) != null) {
            film.setId(updateFilm.getId());
        }
        film.setName(updateFilm.getName());
        film.setDescription(updateFilm.getDescription());
        film.setReleaseDate(updateFilm.getReleaseDate());
        film.setDuration(updateFilm.getDuration());
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    //получение всех фильмов
    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получение списка фильмов)");
        return films.values();
    }

    private void validFilm(Film film) {
        //название не может быть пустым;
        if (film.getName()== null || film.getName().isBlank()) {
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
}
