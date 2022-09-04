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
public class AuthControllerTests {

    @Autowired
    ApplicationContext context;

    @Test
    public void registerSuccess() {

    }

    @Test
    public void registerPreexistingAccount() {

    }

    @Test
    public void registerCreationFailure() {

    }

    @Test
    public void startForgotPasswordSuccess() {

    }

    @Test
    public void loginSuccess() {

    }

    @Test
    public void loginFailure() {

    }

    @Test
    public void loginWithForgotPasswordSuccess() {

    }

    @Test
    public void loginTwoFactorRequiredSuccess() {

    }

    @Test
    public void loginTwoFactorSuppliedSuccess() {

    }

    @Test
    public void passwordChangeRequiredSuccess() {

    }

    @Test
    public void passwordChangeSuppliedSuccess() {

    }

    @Test
    public void logoutSuccess() {

    }
}
