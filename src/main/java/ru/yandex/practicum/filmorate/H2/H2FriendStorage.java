package ru.yandex.practicum.filmorate.H2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.Collection;

@Repository
public class H2FriendStorage implements FriendStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public H2FriendStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addFriend(final Long userId, final Long friendId) {
        final String sql = "INSERT INTO friends (user_id, friend_id) VALUES (:user_id, :friend_id)";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean friendRemove(final Long userId, final Long friendId) {
        final String sql = "DELETE FROM friends WHERE user_id = :user_id AND friend_id = :friend_id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        try {
            return jdbcTemplate.update(sql, params) > 0;
        } catch (EmptyResultDataAccessException ignored) {
            throw new ConditionsNotMetException("Ошибка при проверке существования пользователей.");
        }
    }

    @Override
    public Collection<User> userFriends(final Long userId) {
        final String sql = "SELECT u.* FROM users u INNER JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = :user_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return jdbcTemplate.query(sql, parameterSource, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setName(rs.getString("user_name"));
            user.setLogin(rs.getString("login"));
            user.setEmail(rs.getString("email"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        });
    }

    @Override
    public Collection<User> commonFriends(final Long userIntersectionId, final Long userId) {
        final String sql = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.user_id = f1.friend_id " +
                "JOIN friends f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = :user1_id " +
                "AND f2.user_id = :user2_id";

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user1_id", userIntersectionId)
                .addValue("user2_id", userId);

        return jdbcTemplate.query(sql, parameterSource, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setName(rs.getString("user_name"));
            user.setLogin(rs.getString("login"));
            user.setEmail(rs.getString("email"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        });
    }
}