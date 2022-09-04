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
import io.fusionauth.domain.api.user.ForgotPasswordResponse;
import io.fusionauth.domain.api.user.RegistrationResponse;
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
public class AuthControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mock;
    private MockHttpSession mockHttpSession;
    private SecurityConfig.User user;

    @MockBean
    private FusionAuthClient fusionAuthClient;

    @Test
    public void registerSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // mocks
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 400;
        userResponse.successResponse = Mockito.mock(UserResponse.class);
        Mockito.when(fusionAuthClient.retrieveUserByEmail(Mockito.any())).thenReturn(userResponse);
        ClientResponse<RegistrationResponse, Errors> registrationResponse = new ClientResponse<>();
        registrationResponse.status = 200;
        registrationResponse.successResponse = new RegistrationResponse();
        registrationResponse.successResponse.user = Mockito.mock(User.class);
        Whitebox.setInternalState(registrationResponse.successResponse.user, "id", UUID.randomUUID());
        Mockito.when(fusionAuthClient.register(Mockito.any(), Mockito.any())).thenReturn(registrationResponse);
        Mockito.when(registrationResponse.successResponse.user.getRoleNamesForApplication(Mockito.any())).thenReturn(new HashSet<>());

        var om = new ObjectMapper();
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setName("test");
        registrationDto.setUsername("test");
        registrationDto.setPassword("test");

        // test registration
        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/register").contentType("application/json").content(om.writeValueAsString(registrationDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) != null);
    }

    @Test
    public void registerPreexistingAccount() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // mocks
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = Mockito.mock(User.class);
        Mockito.when(fusionAuthClient.retrieveUserByEmail(Mockito.any())).thenReturn(userResponse);

        var om = new ObjectMapper();
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setName("test");
        registrationDto.setUsername("test");
        registrationDto.setPassword("test");

        // test registration
        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/register").contentType("application/json").content(om.writeValueAsString(registrationDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);
    }

    @Test
    public void startForgotPasswordSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // user lookup
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = Mockito.mock(UserResponse.class);
        Mockito.when(fusionAuthClient.retrieveUserByLoginId(Mockito.any())).thenReturn(userResponse);
        ClientResponse<ForgotPasswordResponse, Errors> forgotPasswordResponse = new ClientResponse<>();
        forgotPasswordResponse.status = 200;
        Mockito.when(fusionAuthClient.forgotPassword(Mockito.any())).thenReturn(forgotPasswordResponse);

        var om = new ObjectMapper();

        var response = mock.perform(MockMvcRequestBuilders.get("/api/auth/forgot-password?user=test").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);
    }

    @Test
    public void startForgotPasswordFailure() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // user lookup
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 400;
        userResponse.successResponse = Mockito.mock(UserResponse.class);
        Mockito.when(fusionAuthClient.retrieveUserByLoginId(Mockito.any())).thenReturn(userResponse);

        var om = new ObjectMapper();

        var response = mock.perform(MockMvcRequestBuilders.get("/api/auth/forgot-password?user=test").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);
    }

    @Test
    public void loginSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // login
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

        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) != null);
    }

    @Test
    public void loginFailure() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // login
        ClientResponse<LoginResponse, Errors> authResponse = new ClientResponse<>();
        authResponse.status = 401;
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(authResponse);

        var om = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test");
        loginDto.setPassword("test");

        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(!json.get("response").get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);
    }

    @Test
    public void loginWithForgotPasswordSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // login
        ClientResponse<LoginResponse, Errors> authResponse = new ClientResponse<>();
        authResponse.status = 401;
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = Mockito.mock(User.class);
        Whitebox.setInternalState(userResponse.successResponse.user, "id", UUID.randomUUID());
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(authResponse);
        Mockito.when(fusionAuthClient.retrieveUserByChangePasswordId(Mockito.any())).thenReturn(userResponse);
        Mockito.when(fusionAuthClient.updateUser(Mockito.any(), Mockito.any())).thenReturn(userResponse);
        Mockito.when(userResponse.successResponse.user.getRoleNamesForApplication(Mockito.any())).thenReturn(new HashSet<>());

        var om = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test");
        loginDto.setForgotPasswordCode("test");
        loginDto.setNewPassword("test");

        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) != null);
    }

    @Test
    public void loginTwoFactorRequiredSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // login
        ClientResponse<LoginResponse, Errors> authResponse = new ClientResponse<>();
        authResponse.status = 242;
        authResponse.successResponse = new LoginResponse();
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(authResponse);

        var om = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test");
        loginDto.setPassword("test");

        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("success").asBoolean());
        assertTrue(json.get("response").get("requiresTwoFactorCode").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);
    }

    @Test
    public void loginTwoFactorSuppliedSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // login
        ClientResponse<LoginResponse, Errors> authResponse = new ClientResponse<>();
        authResponse.status = 242;
        authResponse.successResponse = new LoginResponse();
        authResponse.successResponse.twoFactorId = "test";
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = Mockito.mock(User.class);
        Whitebox.setInternalState(userResponse.successResponse.user, "id", UUID.randomUUID());
        ClientResponse<LoginResponse, Errors> twoFactorAuthResponse = new ClientResponse<>();
        twoFactorAuthResponse.status = 200;
        twoFactorAuthResponse.successResponse = new LoginResponse();
        twoFactorAuthResponse.successResponse.user = Mockito.mock(User.class);
        Whitebox.setInternalState(twoFactorAuthResponse.successResponse.user, "id", UUID.randomUUID());
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(authResponse);
        Mockito.when(fusionAuthClient.retrieveUserByLoginId(Mockito.any())).thenReturn(userResponse);
        Mockito.when(fusionAuthClient.twoFactorLogin(Mockito.any())).thenReturn(twoFactorAuthResponse);
        Mockito.when(userResponse.successResponse.user.getRoleNamesForApplication(Mockito.any())).thenReturn(new HashSet<>());

        var om = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test");
        loginDto.setPassword("test");
        loginDto.setTwoFactorCode("test");

        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) != null);
    }

    @Test
    public void passwordChangeRequiredSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // login
        ClientResponse<LoginResponse, Errors> authResponse = new ClientResponse<>();
        authResponse.status = 203;
        authResponse.successResponse = new LoginResponse();
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(authResponse);

        var om = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test");
        loginDto.setPassword("test");

        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("success").asBoolean());
        assertTrue(json.get("response").get("requiresPasswordChange").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);
    }

    @Test
    public void passwordChangeSuppliedSuccess() throws Exception {
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        // logout first
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);

        // login
        ClientResponse<LoginResponse, Errors> authResponse = new ClientResponse<>();
        authResponse.status = 203;
        authResponse.successResponse = new LoginResponse();
        ClientResponse<UserResponse, Errors> userResponse = new ClientResponse<>();
        userResponse.status = 200;
        userResponse.successResponse = new UserResponse();
        userResponse.successResponse.user = Mockito.mock(User.class);
        Whitebox.setInternalState(userResponse.successResponse.user, "id", UUID.randomUUID());
        Mockito.when(fusionAuthClient.login(Mockito.any())).thenReturn(authResponse);
        Mockito.when(fusionAuthClient.retrieveUserByLoginId(Mockito.any())).thenReturn(userResponse);
        Mockito.when(fusionAuthClient.updateUser(Mockito.any(), Mockito.any())).thenReturn(userResponse);
        Mockito.when(userResponse.successResponse.user.getRoleNamesForApplication(Mockito.any())).thenReturn(new HashSet<>());

        var om = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test");
        loginDto.setPassword("test");
        loginDto.setNewPassword("test");

        var response = mock.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType("application/json").content(om.writeValueAsString(loginDto)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        // check success
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("success").asBoolean());
        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) != null);
    }

    @Test
    public void logoutSuccess() throws Exception {
        // login
        user = new SecurityConfig.User();

        mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
        mockHttpSession = new MockHttpSession(webApplicationContext.getServletContext());
        mockHttpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, user);

        // now test logout
        mock.perform(MockMvcRequestBuilders.get("/api/auth/logout").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertTrue(mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY) == null);
    }
}
