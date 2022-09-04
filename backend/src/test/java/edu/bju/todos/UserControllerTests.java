package edu.bju.todos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.dtos.EmailChangeDto;
import edu.bju.todos.dtos.LoginDto;
import edu.bju.todos.dtos.PasswordChangeDto;
import edu.bju.todos.dtos.RegistrationDto;
import edu.bju.todos.dtos.TwoFactorDto;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.TwoFactorMethod;
import io.fusionauth.domain.User;
import io.fusionauth.domain.UserTwoFactorConfiguration;
import io.fusionauth.domain.api.LoginResponse;
import io.fusionauth.domain.api.TwoFactorResponse;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.twoFactor.SecretResponse;
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

import java.util.ArrayList;
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
    public void getSecretSuccess() throws Exception {
        var om = new ObjectMapper();

        ClientResponse<SecretResponse, Void> secretResponse = new ClientResponse<>();
        secretResponse.status = 200;
        secretResponse.successResponse = new SecretResponse();
        secretResponse.successResponse.secret = "test";
        secretResponse.successResponse.secretBase32Encoded = "test";
        Mockito.when(fusionAuthClient.generateTwoFactorSecret()).thenReturn(secretResponse);

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.get("/api/user/get-secret").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("secret").asText().equals("test"));
        assertTrue(json.get("response").get("secretBase32Encoded").asText().equals("test"));
    }

    @Test
    public void toggleTwoFactorOn() throws Exception {
        var om = new ObjectMapper();

        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = new User();
        Mockito.when(fusionAuthClient.retrieveUser(Mockito.any())).thenReturn(userResponse);
        ClientResponse<TwoFactorResponse, Errors> twoFactorResponse = new ClientResponse<>();
        twoFactorResponse.status = 200;
        Mockito.when(fusionAuthClient.enableTwoFactor(Mockito.any(), Mockito.any())).thenReturn(twoFactorResponse);

        TwoFactorDto twoFactorDto = new TwoFactorDto();
        twoFactorDto.setEnableTwoFactor(true);

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.post("/api/user/toggle-two-factor").contentType("application/json").content(om.writeValueAsString(twoFactorDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void toggleTwoFactorOff() throws Exception {
        var om = new ObjectMapper();

        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = new User();
        userResponse.successResponse.user.twoFactor = new UserTwoFactorConfiguration();
        userResponse.successResponse.user.twoFactor.methods.add(new TwoFactorMethod());
        Mockito.when(fusionAuthClient.retrieveUser(Mockito.any())).thenReturn(userResponse);
        ClientResponse<Void, Errors> disableResponse = new ClientResponse<>();
        disableResponse.status = 200;
        Mockito.when(fusionAuthClient.disableTwoFactor(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(disableResponse);

        TwoFactorDto twoFactorDto = new TwoFactorDto();
        twoFactorDto.setEnableTwoFactor(false);

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.post("/api/user/toggle-two-factor").contentType("application/json").content(om.writeValueAsString(twoFactorDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void changeEmailSuccess() throws Exception {
        var om = new ObjectMapper();

        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = new User();
        userResponse.successResponse.user.twoFactor = new UserTwoFactorConfiguration();
        userResponse.successResponse.user.twoFactor.methods.add(new TwoFactorMethod());
        Mockito.when(fusionAuthClient.retrieveUser(Mockito.any())).thenReturn(userResponse);
        ClientResponse<UserResponse, Errors> updateUserResponse = new ClientResponse<>();
        updateUserResponse.status = 200;
        Mockito.when(fusionAuthClient.updateUser(Mockito.any(), Mockito.any())).thenReturn(updateUserResponse);

        EmailChangeDto emailChangeDto = new EmailChangeDto();

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.post("/api/user/change-email").contentType("application/json").content(om.writeValueAsString(emailChangeDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void changePasswordSuccess() throws Exception {
        var om = new ObjectMapper();

        ClientResponse<LoginResponse, Errors> loginResponse = new ClientResponse<>();
        loginResponse.status = 200;
        loginResponse.successResponse = new LoginResponse();
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(loginResponse);
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = new User();
        userResponse.successResponse.user.twoFactor = new UserTwoFactorConfiguration();
        userResponse.successResponse.user.twoFactor.methods.add(new TwoFactorMethod());
        Mockito.when(fusionAuthClient.retrieveUser(Mockito.any())).thenReturn(userResponse);
        ClientResponse<UserResponse, Errors> updateUserResponse = new ClientResponse<>();
        updateUserResponse.status = 200;
        Mockito.when(fusionAuthClient.updateUser(Mockito.any(), Mockito.any())).thenReturn(updateUserResponse);

        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.post("/api/user/change-password").contentType("application/json").content(om.writeValueAsString(passwordChangeDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void changePasswordFailure() throws Exception {
        var om = new ObjectMapper();

        ClientResponse<LoginResponse, Errors> loginResponse = new ClientResponse<>();
        loginResponse.status = 400;
        loginResponse.successResponse = new LoginResponse();
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(loginResponse);
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = new User();
        userResponse.successResponse.user.twoFactor = new UserTwoFactorConfiguration();
        userResponse.successResponse.user.twoFactor.methods.add(new TwoFactorMethod());
        Mockito.when(fusionAuthClient.retrieveUser(Mockito.any())).thenReturn(userResponse);
        ClientResponse<UserResponse, Errors> updateUserResponse = new ClientResponse<>();
        updateUserResponse.status = 200;
        Mockito.when(fusionAuthClient.updateUser(Mockito.any(), Mockito.any())).thenReturn(updateUserResponse);

        PasswordChangeDto passwordChangeDto = new PasswordChangeDto();

        // test fetch current user
        var response = mock.perform(MockMvcRequestBuilders.post("/api/user/change-password").contentType("application/json").content(om.writeValueAsString(passwordChangeDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }
}