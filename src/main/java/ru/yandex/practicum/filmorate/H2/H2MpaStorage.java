package ru.yandex.practicum.filmorate.H2;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Map;


@Repository
public class H2MpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public H2MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Mpa> findMpaById(Long id) {
        String sql = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = :mpa_id";
        Map<String, Object> params = Map.of("mpa_id", id);
        return jdbcTemplate.query("select mpa_id, mpa_name from public.mpa", (rs, rowNum) -> {
            Long mpa_id = rs.getLong("mpa_id");
            String name = rs.getString("mpa_name");
            return new Mpa(mpa_id, name);
        });
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")));
    }
}

