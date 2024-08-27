package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;


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

        assertNotNull(addUser);
        assertNotNull(addUser.getId());
        assertNotNull(secondUser.getEmail(), addUser.getEmail());
        assertNotNull(secondUser.getLogin(), addUser.getLogin());
        assertNotNull(secondUser.getName(), addUser.getName());
        System.out.println(userController.getAllUsers());

    }

}

