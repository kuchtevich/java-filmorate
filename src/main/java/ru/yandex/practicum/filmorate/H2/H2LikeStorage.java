package ru.yandex.practicum.filmorate.H2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Repository
public class H2LikeStorage implements LikeStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public H2LikeStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean filmLike(Long filmId, Long userId) {
        final String sql = "INSERT INTO likes (film_id, user_id) VALUES (:film_id, :user_id)";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("user_id", userId);
        return jdbcTemplate.update(sql, parameterSource) > 0;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        final String sql = "DELETE FROM likes WHERE film_id = film_id AND user_id = user_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("user_id", userId);
        return jdbcTemplate.update(sql, parameterSource) > 0;
    }
}
