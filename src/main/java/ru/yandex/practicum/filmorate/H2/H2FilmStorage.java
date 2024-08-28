package ru.yandex.practicum.filmorate.H2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.*;


@Repository
public abstract class H2FilmStorage  {

    private final NamedParameterJdbcOperations jdbcTemplate;


    private final MpaStorage jdbcRatingRepository;

    @Autowired
    public H2FilmStorage(NamedParameterJdbcOperations jdbcTemplate, MpaStorage ratingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcRatingRepository = ratingRepository;
    }


    public Film filmAdd(Film filmRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Long ratingId = 0L;
        if (filmRequest.getMpa() != null) {
            ratingId = filmRequest.getMpa().getId();
            checkCorrectMpa(ratingId);
        }
        final String sql = "INSERT INTO films (film_name,description,released,duration,mpa_id) " +
                "VALUES (:film_name,:description,:released,:duration,:mpa_id);";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_name", filmRequest.getName())
                .addValue("description", filmRequest.getDescription())
                .addValue("released", filmRequest.getReleaseDate())
                .addValue("duration", filmRequest.getDuration())
                .addValue("mpa_id", ratingId);

        jdbcTemplate.update(sql, parameterSource, keyHolder);
        filmRequest.setId(keyHolder.getKey().longValue());
        return addExtraFields(filmRequest);
    }


    public Film filmUpdate(Film filmRequest) {
        final String sql = "UPDATE FILMS SET film_name = :film_name, description = :description," +
                " released = :released, duration = :duration, mpa_id = :mpa_id WHERE film_id = :film_id;";
        deleteAllFilmGenresById(filmRequest.getId());
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmRequest.getId())
                .addValue("film_name", filmRequest.getName())
                .addValue("description", filmRequest.getDescription())
                .addValue("released", filmRequest.getReleaseDate())
                .addValue("duration", filmRequest.getDuration())
                .addValue("mpa_id", filmRequest.getMpa().getId());
        jdbcTemplate.update(sql, parameterSource);
        return addExtraFields(filmRequest);
    }


    public Optional<Film> getFilm(Long id) {
        final String sql = "SELECT * FROM films WHERE film_id = :film_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", id);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("released").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            Long mpaId = rs.getLong("mpa_id");
            Optional<Mpa> ratingOptional = jdbcRatingRepository.findRatingById(mpaId);
            ratingOptional.ifPresent(film::setMpa);
            return addExtraFields(film);
        }));
    }


    public boolean filmDelete(Long id) {
        final String sql = "DELETE FROM films WHERE film_id = :film_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", id);
        deleteAllFilmGenresById(id);
        return jdbcTemplate.update(sql, parameterSource) > 0;

    }




    private boolean deleteAllFilmGenresById(Long id) {
        final String sql = "DELETE FROM film_genres WHERE film_id = film_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", id);
        return jdbcTemplate.update(sql, parameterSource) > 0;
    }

    private void filmGenresAdd(Long filmId, Long genreId) {
        checkCorrectGenre(genreId);
        final String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id)";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("genre_id", genreId);
        jdbcTemplate.update(sql, parameterSource);
    }

    private List<Genre> getAllFilmGenresById(Long id) {
        final String sql = "SELECT g.genre_id, g.genre_name " +
                "FROM film_genres fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = :id";

        SqlParameterSource genreParameters = new MapSqlParameterSource()
                .addValue("id", id);
        return jdbcTemplate.query(sql, genreParameters, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        });
    }

    private void checkCorrectGenre(Long id) {
        final String sql = "SELECT genre_id, genre_name AS name FROM genres WHERE genre_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id);
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) ->
                    new Genre(rs.getLong("genre_id"), rs.getString("name"))
            );
        } catch (EmptyResultDataAccessException ignored) {
            throw new ValidationException("Жанр не найден для заданного ID: " + id);
        }
        if (genre.getName().isBlank() || genre.getId() == null) {
            throw new ConditionsNotMetException("Неправильно задан жанр");
        }
    }

}


