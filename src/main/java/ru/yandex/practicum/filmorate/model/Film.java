package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Film.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private LinkedHashSet<Genre> genres;
    private Mpa mpa;
}