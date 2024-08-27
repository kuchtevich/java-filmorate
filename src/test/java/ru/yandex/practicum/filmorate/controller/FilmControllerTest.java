package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;



@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmStorage filmStorage;
    FilmController filmController;
    Film film;

    @Test
    public void testAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        assertNotNull(films);
        assertTrue(films.isEmpty());
    }


}