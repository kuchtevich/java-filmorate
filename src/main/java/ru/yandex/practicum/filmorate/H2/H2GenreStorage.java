package ru.yandex.practicum.filmorate.H2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class H2GenreStorage implements GenreStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public H2GenreStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> genreById(final Long id) {
        final String sql = "SELECT genre_id, genre_name FROM genres WHERE genre_id = :genre_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("genre_id", id);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) ->
                new Genre(rs.getLong("genre_id"), rs.getString("genre_name"))));
    }

    @Override
    public List<Genre> getAllGenre() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
    }
}