package ru.yandex.practicum.filmorate.H2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class H2MpaStorage implements MpaStorage {
    private final NamedParameterJdbcOperations jdbc;

    @Autowired
    public H2MpaStorage(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Mpa> findMpaById(final Long id) {
        final String sql = "SELECT mpa_id, mpa_name AS name FROM mpa WHERE mpa_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id);
        return Optional.ofNullable(jdbc.queryForObject(sql, parameterSource, (rs, rowNum) ->
                new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name"))));
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbc.query(sql, (rs, rowNum) -> new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")));
    }
}