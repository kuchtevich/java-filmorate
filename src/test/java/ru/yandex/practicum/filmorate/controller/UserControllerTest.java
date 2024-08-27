package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserController userController;


//    @Test
//    public void testAddUser() {
//        User newUser = new User();
//        newUser.setLogin("Surname5");
//        newUser.setName("user5");
//        newUser.setEmail("mail5@yandex.com");
//        newUser.setBirthday(LocalDate.of(2020, 8, 19));
//
//        Optional<User> userOptional = Optional.ofNullable(user.addUser(newUser));
//        assertThat(userOptional)
//                .isPresent()
//                .hasValueSatisfying(user -> {
//                            assertThat(user).hasFieldOrPropertyWithValue("id", 5L);
//                            assertThat(user).hasFieldOrPropertyWithValue("login", "Surname5");
//                            assertThat(user).hasFieldOrPropertyWithValue("name", "user5");
//                            assertThat(user).hasFieldOrPropertyWithValue("email",
//                                    "mail5@yandex.com");
//                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
//                                    LocalDate.of(2020, 8, 19));
//                        }
//                )
//    }

}

