package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


@SpringBootTest
public class UserControllerTest {
    @Autowired
    private UserController userController;


    @Test
    public void testAddUser() {
        User secondUser = new User();
        secondUser.setEmail("name@mail.com");
        secondUser.setLogin("User1");
        secondUser.setName("UserOne");
        secondUser.setBirthday(LocalDate.of(1996, 1, 10));

        User addUser = userController.addUser(secondUser);
        Assertions.assertNotNull(addUser);
        Assertions.assertNotNull(addUser.getId());
        Assertions.assertEquals(secondUser.getEmail(), addUser.getEmail());
        Assertions.assertEquals(secondUser.getLogin(), addUser.getLogin());
        Assertions.assertEquals(secondUser.getName(), addUser.getName());
        Assertions.assertEquals(secondUser.getBirthday(), addUser.getBirthday());
    }

}

