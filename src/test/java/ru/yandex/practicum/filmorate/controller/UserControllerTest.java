package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ImportResource
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {

    @Autowired
    UserStorage userStorage;


    @Test
    void shouldUpdateUserTest() {
        User newUser = new User();
        newUser.setId(5L);
        newUser.setLogin("SernameUpdate");
        newUser.setName("Userupdate");
        newUser.setEmail("update@yangex.com");
        newUser.setBirthday(LocalDate.of(2020, 8, 19));

        Optional<User> userOptional = Optional.ofNullable(userStorage.updateUser(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", 5L);
                            assertThat(user).hasFieldOrPropertyWithValue("login", "SernameUpdate");
                            assertThat(user).hasFieldOrPropertyWithValue("name", "Userupdate");
                            assertThat(user).hasFieldOrPropertyWithValue("email",
                                    "update@yangex.com");
                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                    LocalDate.of(2020, 8, 19));
                        }
                );
    }
}