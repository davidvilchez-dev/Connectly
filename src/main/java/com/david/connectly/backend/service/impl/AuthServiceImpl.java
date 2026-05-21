package com.david.connectly.backend.service.impl;

import com.david.connectly.backend.dto.request.LoginRequest;
import com.david.connectly.backend.dto.request.RegisterRequest;
import com.david.connectly.backend.dto.response.AuthResponse;
import com.david.connectly.backend.entity.User;
import com.david.connectly.backend.mapper.UserMapper;
import com.david.connectly.backend.repository.UserRepository;
import com.david.connectly.backend.security.CustomUserDetails;
import com.david.connectly.backend.security.JwtService;
import com.david.connectly.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar si el username o email ya existen (lógica simple)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Crear el usuario y hashear la contraseña
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Guardar en base de datos
        User savedUser = userRepository.save(user);

        // Generar token JWT
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken, userMapper.toResponse(savedUser));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // En este ejemplo usamos el email. Si quieres soportar ambos (username or
        // email),
        // busca en BD primero para saber el email real.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> userRepository.findByUsername(request.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        // Autenticar la contraseña
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), // Usamos el email como identificador principal en Spring Security
                        request.getPassword()));

        // Si pasa la autenticación, generar token
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken, userMapper.toResponse(user));
    }
}