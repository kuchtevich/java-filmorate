package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.H2.H2GenreStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {

    private final H2GenreStorage genreRepository;

    public GenreService(H2GenreStorage genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre genreById(Long id) {
        Optional<Genre> genreOptional = genreRepository.genreById(id);
        if (genreOptional.isPresent()) {
            log.info("Отправлен ответ GET / genres с телом {}", genreOptional.get());
            return genreOptional.get();
        } else {
            log.error("Такого жанра не существует");
            throw new ConditionsNotMetException("Жанра с ID " + id + " не существует");
        }
    }

    public List<Genre> getAllGenre() {
        log.info("Отправлен ответ GET /genres");
        return genreRepository.getAllGenre();
    }
}
