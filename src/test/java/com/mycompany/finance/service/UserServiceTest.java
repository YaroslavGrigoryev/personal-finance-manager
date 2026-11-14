package com.mycompany.finance.service;

import com.mycompany.finance.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    void successfulLoginCheck() {
        UserService userService = new UserService();
        userService.register("Alex", "123456");
        User userAlex = userService.login("Alex", "123456");
        assertNotNull(userAlex);
        assertEquals("Alex", userAlex.getLogin());
    }

    @Test
    void wrongPasswordCheck() {
        UserService userService = new UserService();
        userService.register("Alex", "123456");
        User userAlex = userService.login("Alex", "999999");
        assertNull(userAlex);
    }

    @Test
    void duplicateRegistrationCheck() {
        UserService userService = new UserService();
        userService.register("Alex", "123456");
        userService.register("Alex", "999999");
        assertNotNull(userService.login("Alex", "123456"));
        assertNull(userService.login("Alex", "999999"));
    }
}
