package ru.yandex.practicum.filmorate.H2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;


@Repository
@Primary
public class H2FilmStorage implements FilmStorage {

    private final NamedParameterJdbcOperations jdbcTemplate;


    private final MpaStorage jdbcMpaStorage;

    @Autowired
    public H2FilmStorage(NamedParameterJdbcOperations jdbcTemplate, MpaStorage ratingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcMpaStorage = ratingRepository;
    }

    @Override
    public Film addFilm(Film filmRequest) {
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

    @Override
    public Film updateFilm(Film filmRequest) {
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

    @Override
    public Optional<Film> filmGet(Long id) {
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
            Optional<Mpa> ratingOptional = jdbcMpaStorage.findMpaById(mpaId);
            ratingOptional.ifPresent(film::setMpa);
            return addExtraFields(film);
        }));
    }

    @Override
    public boolean deleteFilm(Long id) {
        final String sql = "DELETE FROM films WHERE film_id = :film_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", id);
        deleteAllFilmGenresById(id);
        return jdbcTemplate.update(sql, parameterSource) > 0;

    }

    @Override
    public Collection<Film> allFilms() {
        final String sql = "SELECT f.film_id, f.film_name, f.description, f.released, f.duration, " +
                "m.mpa_id, m.mpa_name " +
                " FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id ";
        SqlParameterSource parameterSource = new MapSqlParameterSource();
        Collection<Film> films = jdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                createFilmFromResultSet(rs));
        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getGenresFilms();
        for (Film film : films) {
            film.setGenres(filmGenresMap.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
        return films;
    }

    @Override
    public Collection<Film> popularFilms(Long count) {
        final String sql = "SELECT f.film_id, f.film_name, f.description, f.released, f.duration, " +
                "m.mpa_id, m.mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "GROUP BY f.film_id, f.film_name, f.description, f.released, f.duration, m.mpa_id, m.mpa_name " +
                "ORDER BY likes_count DESC " +
                "LIMIT :count";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("count", count);
        Collection<Film> films = jdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                createFilmFromResultSet(rs));
        Map<Long, LinkedHashSet<Genre>> filmGenresMap = getGenresFilms();
        for (Film film : films) {
            film.setGenres(filmGenresMap.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
        return films;
    }

    private Film addExtraFields(Film film) {
        Long filmId = film.getId();
        Long mpaId = film.getMpa().getId();
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> filmGenresAdd(filmId, genre.getId()));
        }
        Optional<Mpa> filmMpa = jdbcMpaStorage.findMpaById(mpaId);
        Set<Genre> filmGenres = new LinkedHashSet<>(getAllFilmGenresById(filmId));
        film.setMpa(filmMpa.orElseThrow(() -> new ConditionsNotMetException("Рейтинг с ID " + mpaId + " не найден")));
        film.setGenres((LinkedHashSet<Genre>) filmGenres);
        return film;
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

    private void checkCorrectMpa(Long id) {
        final String sql = "SELECT mpa_id, mpa_name AS name FROM mpa WHERE mpa_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id);
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) ->
                    new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name"))
            );
        } catch (EmptyResultDataAccessException ignored) {
            throw new ValidationException("Mpa не найден для заданного ID: " + id);
        }
        if (mpa.getName().isBlank() || mpa.getId() == null) {
            throw new ConditionsNotMetException("Неправильно задан рейтинг");
        }
    }

    private Map<Long, LinkedHashSet<Genre>> getGenresFilms() {
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
}


