package ru.yandex.practicum.filmorate.H2;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class H2GenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    public H2GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> genreById(Long id) {
        return jdbcTemplate.queryForObject("genre_id, description, duration, film_name, release from public.film where film_id = ?", (rs, rowNum) -> {
            Long film_id = rs.getLong("film_id");
            String description = rs.getString("description");
            String name = rs.getString("film_name");
            LocalDate releaseDate = rs.getDate("release").toLocalDate();
            Integer duration = rs.getInt("duration");
            return new Film();
        }, id);
    }

    @Override
    public List<Genre> getAllGenre() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
    }


}
