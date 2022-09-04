package edu.bju.todos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.dtos.LoginDto;
import edu.bju.todos.enums.Role;
import edu.bju.todos.enums.Status;
import edu.bju.todos.enums.Type;
import edu.bju.todos.models.Todo;
import edu.bju.todos.services.TodoService;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.LoginResponse;
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
import org.springframework.security.core.context.SecurityContextImpl;
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
public class TodoControllerTests {

    @Autowired
    private TodoService todoService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mock;
    private MockHttpSession mockHttpSession;
    private SecurityConfig.User user;

    @MockBean
    private FusionAuthClient fusionAuthClient;

    @BeforeEach
    public void login() throws Exception {
        user = new SecurityConfig.User();

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
        todoService.save(new Todo(null, "Test 1", "Test 1", Status.PENDING, Type.UNCLASSIFIED, "test@test.com", null));
        todoService.save(new Todo(null, "Test 2", "Test 2", Status.PENDING, Type.CLASSIFIED, "test@test.com", null));
        todoService.save(new Todo(null, "Test 3", "Test 3", Status.PENDING, Type.SECRET, "test@test.com", null));
        todoService.save(new Todo(null, "Test 4", "Test 4", Status.PENDING, Type.TOP_SECRET, "test@test.com", null));
    }

    @AfterEach
    public void logout() {
        todoService.deleteAll();
        mockHttpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY);
    }

    @Test
    public void listSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 1);

        user.getRoles().clear();
        user.getRoles().add(Role.CLASSIFIED.getDescription());

        response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 2);

        user.getRoles().clear();
        user.getRoles().add(Role.SECRET.getDescription());

        response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 3);

        user.getRoles().clear();
        user.getRoles().add(Role.TOP_SECRET.getDescription());

        response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 4);
    }

    @Test
    public void listWithSortSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING?sortBy=id").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 1);
    }

    @Test
    public void listWithPageSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING?page=2").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 0);
    }

    @Test
    public void listWithSearchSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING?search=Test").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 1);
    }

    @Test
    public void getByIdSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 1);

        var id = json.get("response").get("items").get(0).get("id").asLong();

        response = mock.perform(MockMvcRequestBuilders.get("/api/todo/" + id).contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void getByIdFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 1);

        var id = json.get("response").get("items").get(0).get("id").asLong();

        response = mock.perform(MockMvcRequestBuilders.get("/api/todo/" + (id + 100)).contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("success").asBoolean());
    }

    @Test
    public void createUnclassifiedUserSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.PENDING, Type.UNCLASSIFIED, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void createUnclassifiedUserFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.IN_PROGRESS, Type.UNCLASSIFIED, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("success").asBoolean());
    }

    @Test
    public void createClassifiedUserSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.CLASSIFIED.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.PENDING, Type.UNCLASSIFIED, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void createClassifiedUserFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.CLASSIFIED.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.PENDING, Type.SECRET, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("success").asBoolean());
    }

    @Test
    public void createSecretSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.SECRET.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.PENDING, Type.UNCLASSIFIED, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void createSecretFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.SECRET.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.COMPLETE, Type.UNCLASSIFIED, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("success").asBoolean());
    }

    @Test
    public void createTopSecretSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.TOP_SECRET.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.PENDING, Type.UNCLASSIFIED, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void createTopSecretFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.TOP_SECRET.getDescription());

        var todo = new Todo(null, "Test 5", "Test 5", Status.IN_PROGRESS, Type.UNCLASSIFIED, "test@test.com", null);

        var response = mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("success").asBoolean());
    }

    @Test
    public void createAidFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.AID.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/create").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void updateUnclassifiedFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/update").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void updateClassifiedFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.CLASSIFIED.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/update").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void updateSecretFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.SECRET.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/update").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void updateTopSecretFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.TOP_SECRET.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/update").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void updateAidSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.AID.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 4);

        var todo = om.readValue(json.get("response").get("items").get(0).toString(), Todo.class);
        todo.setStatus(Status.IN_PROGRESS);

        response = mock.perform(MockMvcRequestBuilders.post("/api/todo/update").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }

    @Test
    public void updateAidFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.AID.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 4);

        var todo = om.readValue(json.get("response").get("items").get(0).toString(), Todo.class);
        for(int i = 0; i < 20; ++i) {
            todo.setTitle(todo.getTitle() + todo.getTitle());
        }

        response = mock.perform(MockMvcRequestBuilders.post("/api/todo/update").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(!json.get("success").asBoolean());
    }

    @Test
    public void deleteUnclassifiedFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.UNCLASSIFIED.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/delete").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void deleteClassifiedFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.CLASSIFIED.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/delete").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void deleteSecretFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.SECRET.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/delete").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void deleteTopSecretFailure() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.TOP_SECRET.getDescription());

        mock.perform(MockMvcRequestBuilders.post("/api/todo/delete").contentType("application/json").content(om.writeValueAsString(new Todo())).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void deleteAidSuccess() throws Exception {
        var om = new ObjectMapper();

        var context = (SecurityContextImpl) mockHttpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        var user = (SecurityConfig.User) context.getAuthentication();
        user.getRoles().add(Role.AID.getDescription());

        var response = mock.perform(MockMvcRequestBuilders.get("/api/todo/list/PENDING").contentType("application/json").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        var json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
        assertTrue(json.get("response").get("items").size() == 4);

        var todo = om.readValue(json.get("response").get("items").get(0).toString(), Todo.class);

        response = mock.perform(MockMvcRequestBuilders.post("/api/todo/delete").contentType("application/json").content(om.writeValueAsString(todo)).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        json = om.readTree(response.getResponse().getContentAsString());
        assertTrue(json.get("success").asBoolean());
    }
}
