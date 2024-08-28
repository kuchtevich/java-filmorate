package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.H2.H2GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final H2GenreStorage genreStorage;

    public GenreService(H2GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre genreById(final Long id) {
        try {
            return genreStorage.genreById(id).orElseThrow(() ->
                    new ConditionsNotMetException("Жанра с ID " + id + " не существует"));
        } catch (EmptyResultDataAccessException ignored) {
            throw new ConditionsNotMetException("Жанра с ID " + id + " не существует");
        }
    }

    public List<Genre> getAllGenre() {
        log.info("Отправлен ответ GET /genres");
        return genreStorage.getAllGenre();
    }
}

