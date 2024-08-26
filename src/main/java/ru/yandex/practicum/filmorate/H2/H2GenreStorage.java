package ru.yandex.practicum.filmorate.H2;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class H2GenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public H2GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> genreById(Long id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("genre_id, name from public.genre where genre_id = ?", (rs, rowNum) -> {
            Long genreId = rs.getLong("genre_id");
            String name = rs.getString("name");
            return new Genre();
        }, id));
    }

    @Override
    public List<Genre> getAllGenre() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
    }


}
