package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

@Service
@Slf4j
public class ValidateService {

    private final UserStorage userStorage;

    private final FilmStorage filmStorage;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ValidateService(UserService userService, FilmService filmService,
                           NamedParameterJdbcTemplate jdbcTemplate) {
        this.userStorage = userService;
        this.filmStorage = filmService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void validateFilmInput(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка в названии фильма!");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Превышена длина описания!");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minReleaseDate)) {
            log.error("Ошибка в дате релиза!");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.error("Продолжительность отрицательная!");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    public void validateUserInput(User user) {
        if (user.getLogin() == null || user.getEmail() == null || user.getBirthday() == null) {
            log.error("Ошибка в запросе");
            throw new ValidationException("Переданно нулевое значение");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка в написании почты");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка в написании логина");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка в дате рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public void validateFilmUpdate(Film film) {
        if (film.getId() == null) {
            log.error("Передан пустой ID");
            throw new ConditionsNotMetException("ID не существует");
        }

        final String sql = "SELECT COUNT(*) FROM films WHERE film_id = :film_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        try {
            Integer count = jdbcTemplate.queryForObject(sql, parameterSource, Integer.class);
            if (count == null || count == 0) {
                log.error("Фильма с ID {} не существует", film.getId());
                throw new ConditionsNotMetException("Фильма с ID " + film.getId() + " не существует");
            } else {
                log.info("Фильм с ID {} успешно найден", film.getId());
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при выполнении запроса для проверки существования фильма с ID {}", film.getId(), e);
            throw new ConditionsNotMetException("Ошибка при проверке существования фильма с ID " + film.getId());
        } catch (DataAccessException e) {
            log.error("Ошибка доступа к данным при проверке существования фильма с ID {}", film.getId(), e);
            throw new ConditionsNotMetException("Ошибка доступа к данным при проверке существования фильма с ID " + film.getId());
        }
    }


    public void validUserUpdate(User user) {
        if (user.getId() == null) {
            log.error("Пользователь с таким ID не существует");
            throw new ConditionsNotMetException("Пользователя с ID " + user.getId() + " не существует");
        }
        final String sql = "SELECT * FROM users WHERE user_id = :user_id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", user.getId());

        try {
            jdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) -> {
                User userExist = new User();
                userExist.setId(rs.getLong("user_id"));
                userExist.setName(rs.getString("user_name"));
                userExist.setLogin(rs.getString("login"));
                userExist.setEmail(rs.getString("email"));
                userExist.setBirthday(rs.getDate("birthday").toLocalDate());
                return userExist;
            });
        } catch (EmptyResultDataAccessException ignored) {
            throw new ConditionsNotMetException("Пользователь не найден для заданного ID: " + user.getId());
        }
    }

    public void checkAlreadyFriend(Long userId, Long friendId) {
        final String friendCheckSql = "SELECT COUNT(*) FROM friends WHERE (user_id = :user_id AND friend_id = :friend_id) OR (user_id = :friend_id AND friend_id = :user_id)";
        SqlParameterSource friendCheckParams = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        checkCorrectUser(userId);
        checkCorrectUser(friendId);
        try {
            if (jdbcTemplate.queryForObject(friendCheckSql, friendCheckParams, Integer.class) > 0) {
                throw new ValidationException("Пользователи уже являются друзьями: " + userId + " и " + friendId);
            }
        } catch (EmptyResultDataAccessException ignored) {
            throw new ConditionsNotMetException("Ошибка при запросе к базе данных.");
        }
    }

    public void checkCorrectUser(Long userId) {
        final String userExistSql = "SELECT COUNT(*) FROM users WHERE user_id = :id";
        try {
            SqlParameterSource userParams = new MapSqlParameterSource().addValue("id", userId);
            if (jdbcTemplate.queryForObject(userExistSql, userParams, Integer.class) == 0) {
                throw new ConditionsNotMetException("Пользователь с ID: " + userId + " не найден.");
            }
        } catch (EmptyResultDataAccessException ignored) {
            throw new ConditionsNotMetException("Ошибка при запросе к базе данных.");
        }
    }
}







