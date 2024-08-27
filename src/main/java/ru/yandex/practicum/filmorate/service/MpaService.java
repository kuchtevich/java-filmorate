package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.H2.H2MpaStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;


@Service
@Slf4j
public class MpaService {

    private final H2MpaStorage ratingRepository;

    public MpaService(H2MpaStorage  ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Mpa findRatingById(Long id) {
        List<Mpa> mpaOptional = ratingRepository.findMpaById(id);
        if (mpaOptional.isEmpty()) {
            log.info("Отправлен ответ GET /mpa с телом {}", mpaOptional.get(id.intValue()));
            return mpaOptional.get(id.intValue());
        } else {
            log.error("Такого mpa не существует");
            throw new ConditionsNotMetException("Mpa с ID " + id + " не существует");
        }
    }

    public List<Mpa> getAllMpa() {
        log.info("Отправлен ответ GET /mpa");
        return ratingRepository.getAllMpa();
    }
}
