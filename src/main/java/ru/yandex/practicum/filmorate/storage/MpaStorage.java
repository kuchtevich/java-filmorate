package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    Optional<Mpa> findMpaById(Long id);

    List<Mpa> getAllMpa();
}

