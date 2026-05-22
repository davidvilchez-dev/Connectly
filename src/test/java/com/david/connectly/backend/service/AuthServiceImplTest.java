package com.david.connectly.backend.service;

import com.david.connectly.backend.dto.request.LoginRequest;
import com.david.connectly.backend.dto.request.RegisterRequest;
import com.david.connectly.backend.dto.response.AuthResponse;
import com.david.connectly.backend.dto.response.UserResponse;
import com.david.connectly.backend.entity.User;
import com.david.connectly.backend.mapper.UserMapper;
import com.david.connectly.backend.repository.UserRepository;
import com.david.connectly.backend.security.CustomUserDetails;
import com.david.connectly.backend.security.JwtService;
import com.david.connectly.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl - Pruebas unitarias")
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("david@test.com");
        user.setUsername("david");
        user.setPassword("hashed_password");

        userResponse = new UserResponse();
    }

    // ─── REGISTER ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: registra usuario nuevo y retorna token")
    void register_shouldReturnAuthResponse_whenUserIsNew() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("david@test.com");
        request.setUsername("david");
        request.setPassword("1234");

        when(userRepository.findByEmail("david@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("david")).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("1234")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        AuthResponse result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register: lanza excepción si el email ya está registrado")
    void register_shouldThrow_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("david@test.com");
        request.setUsername("david");

        when(userRepository.findByEmail("david@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register: lanza excepción si el username ya está tomado")
    void register_shouldThrow_whenUsernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nuevo@test.com");
        request.setUsername("david");

        when(userRepository.findByEmail("nuevo@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("david")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is already taken");
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: retorna token cuando las credenciales son válidas")
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("david@test.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("david@test.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        AuthResponse result = authService.login(request);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("login: lanza excepción cuando el usuario no existe")
    void login_shouldThrow_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("noexiste@test.com");
        request.setPassword("1234");

        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
