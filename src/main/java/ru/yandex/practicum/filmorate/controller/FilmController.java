package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.Collection;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    private final ValidateService validateService;

    @Autowired
    public FilmController(FilmService filmService, ValidateService validateService) {
        this.filmService = filmService;
        this.validateService = validateService;
    }

    @PostMapping
    public Film filmAdd(@RequestBody Film filmRequest) {
        validateService.validateFilmInput(filmRequest);
        return filmService.filmAdd(filmRequest);
    }

    @PutMapping("{filmId}/like/{userId}")
    public void filmLike(@PathVariable final Long filmId, @PathVariable final Long userId) {
        filmService.filmLike(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public void filmLikeRemove(@PathVariable final Long filmId, @PathVariable final Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> popularFilms(@RequestParam(defaultValue = "10") final Long count) {
        return filmService.getPopular(count);
    }

    @PutMapping
    public Film filmUpdate(@RequestBody Film newFilm) {
        validateService.validateFilmUpdate(newFilm);
        return filmService.filmUpdate(newFilm);
    }

    @GetMapping(value = {"{filmId}"})
    public Film getFilm(@PathVariable Long filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping
    public Collection<Film> allFilms() {
        return filmService.allFilms();
    }

    @DeleteMapping(value = {"{filmId}"})
    public void filmDelete(@PathVariable final Long filmId) {
        filmService.filmDelete(filmId);
    }
}