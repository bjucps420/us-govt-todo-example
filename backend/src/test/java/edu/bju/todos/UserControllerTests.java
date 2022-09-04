package edu.bju.todos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.dtos.LoginDto;
import edu.bju.todos.dtos.RegistrationDto;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.LoginResponse;
import io.fusionauth.domain.api.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mock;
    private MockHttpSession mockHttpSession;

    @MockBean
    private FusionAuthClient fusionAuthClient;

    @BeforeEach
    public void login() throws Exception {
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());

        ClientResponse<LoginResponse, Errors> authResponse = new ClientResponse<>();
        authResponse.status = 200;
        authResponse.successResponse = new LoginResponse();
        authResponse.successResponse.user = Mockito.mock(User.class);
        Whitebox.setInternalState(authResponse.successResponse.user, "id", UUID.randomUUID());
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(authResponse);
        Mockito.when(authResponse.successResponse.user.getRoleNamesForApplication(Mockito.any())).thenReturn(new HashSet<>());

        var om = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test");
        loginDto.setPassword("test");

        mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @AfterEach
    public void logout() {
        mockHttpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY);
    }

    @Test
    public void currentNoUser() throws Exception {
        mockHttpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY);

        var om = new ObjectMapper();

        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = Mockito.mock(User.class);
        Mockito.when(fusionAuthClient.retrieveUser(Mockito.any())).thenReturn(userResponse);

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.get("/api/user/current").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.asText().isBlank());
    }

    @Test
    public void currentUserLoggedIn() throws Exception {
        var om = new ObjectMapper();

        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = Mockito.mock(User.class);
        Mockito.when(fusionAuthClient.retrieveUser(Mockito.any())).thenReturn(userResponse);

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.get("/api/user/current").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("user").isNull());
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