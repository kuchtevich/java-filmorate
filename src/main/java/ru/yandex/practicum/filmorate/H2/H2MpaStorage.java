package ru.yandex.practicum.filmorate.H2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class H2MpaStorage implements MpaStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public H2MpaStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> findMpaById(Long id) {
        String sql = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = :mpa_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("mpa_id", id);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) ->
                new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name"))));
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")));
    }
}