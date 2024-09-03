package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa findRatingById(final Long id) {
        try {
            return mpaStorage.findMpaById(id).orElseThrow(() ->
                    (new NotFoundException("Mpa с ID " + id + " не существует")));
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Mpa с ID " + id + " не существует");
        }
    }

    public List<Mpa> getAllMpa() {
        log.info("Отправлен ответ GET /mpa");
        return mpaStorage.getAllMpa();
    }
}
