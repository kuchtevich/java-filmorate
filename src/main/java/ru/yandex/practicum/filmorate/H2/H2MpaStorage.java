package ru.yandex.practicum.filmorate.H2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations

import java.util.List;
import java.util.Optional;


@Repository
public class H2MpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    public H2MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Optional<Mpa> findRatingById(Long id) {
        final String sql = "SELECT mpa_id, mpa_name AS name FROM mpa WHERE mpa_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id);
        try {
            Mpa rating = JdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                    new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name"))
            );
            return Optional.of(rating);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa;";
        return jdbc.query(sql, (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }
}

