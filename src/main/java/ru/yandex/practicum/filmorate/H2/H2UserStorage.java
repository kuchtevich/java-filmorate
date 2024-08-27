package ru.yandex.practicum.filmorate.H2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Repository("H2UserStorage")
public class H2UserStorage implements UserStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public H2UserStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Map<Long, Set<User>> getFriends() {
        return null;
    }

    @Override
    public User addUser(User user) {
        final String INSERT_MESSAGE_SQL = "INSERT INTO users (user_name,login,email,birthday) " +
                "VALUES (:user_name,:login,email,:birthday)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("birhday", user.getBirthday());
        jdbcTemplate.update(INSERT_MESSAGE_SQL, parameterSource, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        final String sql = "UPDATE users SET user_name =:user_name,login =:login," +
                "email=:email,birthday=:birthday WHERE user_id = :user_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("user_name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("birthday", user.getBirthday());
        jdbcTemplate.update(sql, parameterSource);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        final String sql = "DELETE FROM users WHERE user_id = :user_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", id);
        jdbcTemplate.update(sql, parameterSource);
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
        final String sql = "SELECT user_id,user_name,email,login,birthday FROM users WHERE user_id = :user_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", id);
        return jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) -> {
            String email = rs.getString("email");
            String login = rs.getString("login");
            String name = rs.getString("user_name");
            LocalDate birthday = rs.getDate("birthday").toLocalDate();
            return new User(id, email, login, name, birthday);
        });
    }

    public void userAddFriend(Long userId, Long friendId) {
        final String sql = "INSERT INTO friends (user_id,friend_id) VALUES (:user_id,:friend_id)";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        jdbcTemplate.update(sql, parameterSource);
    }

    public void friendRemove(Long userId, Long friendId) {
        final String sql = "DELETE FROM friends WHERE user_id = :user_id AND friend_id = :friend_id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        jdbcTemplate.update(sql, params);
    }

    public Collection<User> userFriends(Long id) {
        final String sql = "SELECT u.* FROM users u INNER JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = :user_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", id);
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

    public Collection<User> commonFriends(Long userIntersectionId, Long userId) {
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
