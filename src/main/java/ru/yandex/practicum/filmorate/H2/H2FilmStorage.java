package ru.yandex.practicum.filmorate.H2;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository("H2FilmStorage")
public class H2FilmStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public H2FilmStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (film.getMpa().getId() == null) {
            throw new NotFoundException("Рейтинг не корректный");
        }
        final String sql = "INSERT INTO films (film_name,description,released,duration,mpa_id) " +
                "VALUES (:film_name,:description,:released,:duration,:mpa_id);";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("released", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId());
        jdbcTemplate.update(sql, parameterSource, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        if (film.getFilmGenres() != null) {
            List<Long> genresId = film.getFilmGenres().stream().map(genre -> genre.geId()).toList();
            addFilmGenres(film.getId(), genresId);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final String DELETE = "DELETE FROM film_genres WHERE film_id = film_id";
        SqlParameterSource parameterSource1 = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        jdbcTemplate.update(DELETE, parameterSource1);
        final String sql = "UPDATE films SET film_name =:film_name,description =:description," +
                "released=:releaseDate,duration=:duration,mpa_id WHERE film_id = :film_id";
        SqlParameterSource parameterSource2 = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("film_name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("duration", film.getDuration())
                .addValue("release", film.getReleaseDate())
                .addValue("mpa_id", film.getMpa().getMId());
        if (film.getFilmGenres() != null) {
            List<Long> genresId = film.getFilmGenres().stream().map(genre -> genre.getGenresId()).toList();
            addFilmGenres(film.getId(), genresId);
        }
        jdbcTemplate.update(sql, parameterSource2);
        return film;
    }
    @Override
    public Film getFilm(Long id) {
        final String sql = "SELECT f.film_id,f.film_name,f.description,f.released,f.duration,m.mpa_name,m.mpa_id FROM films f" +
                "JOIN mpa m ON f.film_id = m.film_id WHERE film_id = :film_id;";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", id);
        return jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("released").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
            return film;
        });
    }

    @Override
    public Collection<Film> getAllFilms() {
        final String sql = "SELECT * FROM films";
        SqlParameterSource parameterSource = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, parameterSource, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("released").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            Long mpaId = rs.getLong("mpa_id");
            Optional<Rating> ratingOptional = jdbcRatingRepository.findRatingById(mpaId);
            ratingOptional.ifPresent(film::setMpa);
            return addExtraFields(film);
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

    private void addFilmGenres(Long id, List<Long> genresId) {
        if (id == null || genresId.isEmpty()) {
            return;
        }
        final String sql = "INSERT INTO film_genres (film_id,genre_id) VALUES (:film_id,:genre_id)";
        SqlParameterSource parameterSource = null;
        for (Long genre : genresId) {
            parameterSource = new MapSqlParameterSource()
                    .addValue("film_id", id)
                    .addValue("genre_id", genre);
        }
        jdbcTemplate.update(sql, parameterSource);
    }
}