package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
        assertEquals(secondUser.getEmail(), addUser.getEmail());
        assertEquals(secondUser.getLogin(), addUser.getLogin());
        assertEquals(secondUser.getName(), addUser.getName());
        assertEquals(secondUser.getBirthday(), addUser.getBirthday());
        System.out.println(userController.getAllUsers());

    }

}

