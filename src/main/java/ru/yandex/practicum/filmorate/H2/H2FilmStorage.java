package ru.yandex.practicum.filmorate.H2;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Repository("H2FilmStorage")
public class H2FilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public H2FilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        final String INSERT_MESSAGE_SQL = "insert into public.users (film_name, description, duration, releaseDate) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_MESSAGE_SQL, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));

            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final String sql = "UPDATE film SET film_name =:film_name,description =:description," +
                "release=:releaseDate,duration=:duration WHERE film_id = :film_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("film_name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("duration", film.getDuration())
                .addValue("release", film.getReleaseDate());
        jdbcTemplate.update(sql, parameterSource);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        return jdbcTemplate.queryForObject("film_id, description, duration, film_name, release from public.film where film_id = ?", (rs, rowNum) -> {
            Long filmId = rs.getLong("film_id");
            String description = rs.getString("description");
            String name = rs.getString("film_name");
            LocalDate releaseDate = rs.getDate("release").toLocalDate();
            Integer duration = rs.getInt("duration");
            return new Film();
        }, id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query("select film_id, description, duration, film_name, release from public.film", (rs, rowNum) -> {
            Long id = rs.getLong("film_id");
            String description = rs.getString("description");
            String name = rs.getString("film_name");
            LocalDate releaseDate = rs.getDate("release").toLocalDate();
            Integer duration = rs.getInt("duration");
            return new Film();
        });
    }

    @Override
    public void filmDelete(Long id) {
        jdbcTemplate.update("delete from public.films where film_id = ?", id);
    }

    @Override
    public Map<Long, Set<Long>> getLikes() {
        return Map.of();
    }
}

