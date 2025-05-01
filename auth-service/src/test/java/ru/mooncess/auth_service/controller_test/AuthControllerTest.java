package ru.mooncess.auth_service.controller_test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mooncess.auth_service.config.SecurityConfig;
import ru.mooncess.auth_service.controller.AuthController;
import ru.mooncess.auth_service.domain.JwtRequest;
import ru.mooncess.auth_service.domain.JwtResponse;
import ru.mooncess.auth_service.domain.RegistrationRequest;
import ru.mooncess.auth_service.domain.User;
import ru.mooncess.auth_service.exception.AuthException;
import ru.mooncess.auth_service.service.AuthService;
import ru.mooncess.auth_service.service.JwtProvider;
import ru.mooncess.auth_service.service.UserService;

import java.util.Optional;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtProvider jwtProvider;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void whenValidCredentials_thenReturnsJwtResponse() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLogin("username");
        request.setPassword("password");

        JwtResponse response = new JwtResponse("accessToken", "refreshToken");

        when(authService.userLogin(any(JwtRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().stringValues("Set-Cookie", containsInAnyOrder(
                        containsString("access="),
                        containsString("refresh=")
                )))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void whenInvalidCredentials_thenReturnsUnauthorized() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLogin("wrongUser");
        request.setPassword("wrongPassword");

        when(authService.userLogin(any(JwtRequest.class))).thenThrow(new AuthException("The user was not found"));

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void logout_shouldClearCookiesAndReturnNoContent() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/v1/logout"))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertEquals(0, response.getCookie("access").getMaxAge());
        assertEquals(0, response.getCookie("refresh").getMaxAge());

        assertEquals("/", response.getCookie("access").getPath());
        assertEquals("/api/v1", response.getCookie("refresh").getPath());

        assertTrue(response.getCookie("access").isHttpOnly());
        assertTrue(response.getCookie("refresh").isHttpOnly());
    }

    @Test
    void createNewUser_shouldReturnCreated() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("newuser");
        registrationRequest.setPassword("password123");
        registrationRequest.setNickname("testuser");

        User createdUser = new User();

        String requestBody = objectMapper.writeValueAsString(registrationRequest);

        when(authService.createNewUser(any(RegistrationRequest.class))).thenReturn(Optional.of(createdUser));

        mockMvc.perform(post("/api/v1/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void createNewUser_shouldReturnBadRequest() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("newuser");
        registrationRequest.setPassword("password123");
        registrationRequest.setNickname("test");

        String requestBody = objectMapper.writeValueAsString(registrationRequest);

        when(authService.createNewUser(any(RegistrationRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1//registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"A user with the specified email address already exists\"}"));
    }

    @Test
    public void testGetNewAccessToken_RefreshTokenNull() throws Exception {
        mockMvc.perform(post("/api/v1/token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetNewAccessToken_Success() throws Exception {
        String refreshToken = "refresh-token";

        JwtResponse jwtResponse = new JwtResponse("new-access-token", null);

        when(authService.getAccessToken(any(String.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/v1/token")
                        .cookie(new Cookie("refresh", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("access=")))
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetNewAccessAndRefreshTokens_RefreshTokenNull() throws Exception {
        mockMvc.perform(post("/api/v1/refresh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetNewAccessAndRefreshTokens_Success() throws Exception {
        String refreshToken = "refresh-token";

        JwtResponse jwtResponse = new JwtResponse("new-access-token", "new-refresh-token");

        when(authService.refresh(any(String.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/v1/refresh")
                        .cookie(new Cookie("refresh", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Set-Cookie", containsInAnyOrder(
                        containsString("access="),
                        containsString("refresh=")
                )))
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }
}
