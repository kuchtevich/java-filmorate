package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public abstract class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Getter
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    private long currentId;

    @Override
    public Film filmAdd(Film filmRequest) {
        log.info("Пришел Post запрос /films с телом {}", filmRequest);
        validateFilmInput(filmRequest);
        Film film = setFilm(filmRequest);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        log.info("Отправлен ответ Post / films с телом {}", filmRequest);
        return film;
    }

    @Override
    public Collection<Film> allFilms() {
        log.info("Отправлен ответ Get / films с телом {}", films.values());
        return films.values();
    }

    @Override
    public Film filmUpdate(Film newFilm) {
        log.info("Пришел Put запрос /films с телом {}", newFilm);
        if (newFilm.getId() == null || films.get(newFilm.getId()) == null) {
            log.error("Фильм с id " + newFilm.getId() + " не найден");
            throw new ConditionsNotMetException("id не найден");
        }
        Film oldFilm = setFilm(newFilm);
        validateFilmInput(newFilm);
        films.remove(newFilm.getId());
        films.put(oldFilm.getId(), oldFilm);
        log.info("Отправлен ответ Put / films с телом {}", oldFilm);
        return oldFilm;
    }

//    //@Override
//    public Film getFilm(Long id) {
//        if (films.get(id) == null) {
//            throw new ConditionsNotMetException("id не найден");
//        }
//        return films.get(id);
//    }

    @Override
    public boolean filmDelete(Long id) {
        if (films.get(id) == null) {
            throw new ConditionsNotMetException("id не найден");
        }
        log.info("Фильм удален");
        films.remove(id);
        return true;
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

    private void validateFilmInput(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка в названии фильма!");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Превышена длина описания!");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minReleaseDate)) {
            log.error("Ошибка в дате релиза!");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность отрицательная!");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private long getNextId() {
        return ++currentId;
    }
}
