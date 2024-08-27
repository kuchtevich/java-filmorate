package ru.yandex.practicum.filmorate.H2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


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
                .addValue("mpa_id", film.getMpa().getId());
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
        final String sql = "SELECT f.film_id, f.film_name, f.description, f.released, f.duration, " +
                "m.mpa_id, m.mpa_name " +
                " FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id ";
        SqlParameterSource parameterSource = new MapSqlParameterSource();
        Collection<Film> films = jdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                createFilmFromResultSet(rs));
        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getAllGenresForFilms();
        for (Film film : films) {
            film.setGenres(filmGenresMap.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
        return films;
    }



    @Override
    public void filmDelete(Long id) {
        final String sql = "DELETE FROM film_genres WHERE film_id = film_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", id);
        jdbcTemplate.update(sql, parameterSource);
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

    private Film createFilmFromResultSet(ResultSet rs) throws SQLException {
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
    }

    private Map<Long, LinkedHashSet<Genre>> getAllGenresForFilms() {
        final String sql = "SELECT fg.film_id, g.genre_id, g.genre_name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id";

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, LinkedHashSet<Genre>> genresMap = new LinkedHashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                long genreId = rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");
                Genre genre = new Genre(genreId, genreName);
                if (!genresMap.containsKey(filmId)) {
                    genresMap.put(filmId, new LinkedHashSet<>());
                }
                genresMap.get(filmId).add(genre);
            }
            return genresMap;
        });
    }
}