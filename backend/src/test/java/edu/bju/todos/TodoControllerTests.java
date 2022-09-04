package edu.bju.todos;

import edu.bju.todos.config.SecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TodoControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mock;
    private MockHttpSession mockHttpSession;
    private SecurityConfig.User user;

    @BeforeEach
    public void login() {
        user = new SecurityConfig.User();

        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mockHttpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, user);
    }

    @AfterEach
    public void logout() {
        mockHttpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY);
    }

    @Test
    public void listSuccess() {

    }

    @Test
    public void listWithSortSuccess() {

    }

    @Test
    public void listWithPageSuccess() {

    }

    @Test
    public void listWithSearchSuccess() {

    }

    @Test
    public void getByIdSuccess() {

    }

    @Test
    public void getByIdFailure() {

    }

    @Test
    public void createUnclassifiedUserSuccess() {

    }

    @Test
    public void createUnclassifiedUserFailure() {

    }

    @Test
    public void createClassifiedUserSuccess() {

    }

    @Test
    public void createClassifiedUserFailure() {

    }

    @Test
    public void createSecretSuccess() {

    }

    @Test
    public void createSecretFailure() {

    }

    @Test
    public void createTopSecretSuccess() {

    }

    @Test
    public void createTopSecretFailure() {

    }

    @Test
    public void createAidFailure() {

    }

    @Test
    public void updateUnclassifiedFailure() {

    }

    @Test
    public void updateClassifiedFailure() {

    }

    @Test
    public void updateSecretFailure() {

    }

    @Test
    public void updateTopSecretFailure() {

    }

    @Test
    public void updateAidSuccess() {

    }

    @Test
    public void updateAidFailure() {

    }

    @Test
    public void deleteUnclassifiedFailure() {

    }

    @Test
    public void deleteClassifiedFailure() {

    }

    @Test
    public void deleteSecretFailure() {

    }

    @Test
    public void deleteTopSecretFailure() {

    }

    @Test
    public void deleteAidSuccess() {

    }

    @Test
    public void deleteAidFailure() {

    }
}
