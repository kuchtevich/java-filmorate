package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.assertEquals;



@SpringBootTest
public class FilmControllerTest {
    FilmController filmController;
    Film film;

//    @BeforeEach
//    public void beforeEach() {
//        filmController = new FilmController();
//        film = new Film();
//        film.setName("Мстители");
//        film.setDescription("Спасение Земли от инопланетян");
//        film.setReleaseDate(LocalDate.of(1996, 01, 10));
//        film.setDuration(100);
//    }

//    @Test
//    @DisplayName("Проверка на добавление фильма")
//    public void testAddFilm() {
//        filmController.filmAdd(film);
//        assertEquals(1, film.getId(), "Фильм не добавлен");
//    }


}