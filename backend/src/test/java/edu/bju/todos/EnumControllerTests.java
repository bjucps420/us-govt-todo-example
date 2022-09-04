package edu.bju.todos;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.controllers.EnumController;
import edu.bju.todos.enums.Status;
import edu.bju.todos.enums.Type;
import liquibase.pro.packaged.T;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnumControllerTests {

    @Autowired
    private EnumController enumController;

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
    public void statusesSuccess() throws Exception {
        var result = mock.perform(MockMvcRequestBuilders.get("/api/enum/status/all").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertTrue(json.isArray());
        assertTrue(json.get(0).asText().equals(Status.PENDING.getDescription()));
        assertTrue(json.get(1).asText().equals(Status.IN_PROGRESS.getDescription()));
        assertTrue(json.get(2).asText().equals(Status.COMPLETE.getDescription()));
    }

    @Test
    public void typesSuccess() throws Exception {
        var result = mock.perform(MockMvcRequestBuilders.get("/api/enum/type/all").session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        var json = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        assertTrue(json.isArray());
        assertTrue(json.get(0).asText().equals(Type.UNCLASSIFIED.getDescription()));
        assertTrue(json.get(1).asText().equals(Type.CLASSIFIED.getDescription()));
        assertTrue(json.get(2).asText().equals(Type.SECRET.getDescription()));
        assertTrue(json.get(3).asText().equals(Type.TOP_SECRET.getDescription()));
    }
}
