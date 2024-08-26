package ru.yandex.practicum.filmorate.H2;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Repository("H2UserStorage")
public class H2UserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public H2UserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Set<User>> getFriends() {
        return Map.of();
    }

    @Override
    public User addUser(User user) {
        final String INSERT_MESSAGE_SQL = "insert into public.users (user_name, login, email, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_MESSAGE_SQL, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getEmail());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));

            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update("delete from public.users where user_id = ?", id);
    }

    @Override
    public Collection<User> allUsers() {
        return jdbcTemplate.query("select user_id, email, login, user_name, birthday from public.users", (rs, rowNum) -> {
            Long id = rs.getLong("user_id");
            String email = rs.getString("email");
            String login = rs.getString("login");
            String name = rs.getString("user_name");
            LocalDate birthday = rs.getDate("birthday").toLocalDate();
            return new User(id, email, login, name, birthday);
        });
    }

    @Override
    public User userGet(Long id) {
        return jdbcTemplate.queryForObject("select user_id, email, login, user_name, birthday from public.users where user_id = ?", (rs, rowNum) -> {
            Long userId = rs.getLong("user_id");
            String email = rs.getString("email");
            String login = rs.getString("login");
            String name = rs.getString("user_name");
            LocalDate birthday = rs.getDate("birthday").toLocalDate();
            return new User(userId, email, login, name, birthday);
        }, id);
    }
}

