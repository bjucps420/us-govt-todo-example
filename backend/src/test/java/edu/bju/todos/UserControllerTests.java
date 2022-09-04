package edu.bju.todos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    ApplicationContext context;

    @Test
    public void currentNoUser() {

    }

    @Test
    public void currentUserLoggedIn() {

    }

    @Test
    public void getSecretSuccess() {

    }

    @Test
    public void toggleTwoFactorOn() {

    }

    @Test
    public void toggleTwoFactorOff() {

    }

    @Test
    public void changeEmailSuccess() {

    }

    @Test
    public void changePasswordSuccess() {

    }

    @Test
    public void changePasswordFailure() {

    }
}