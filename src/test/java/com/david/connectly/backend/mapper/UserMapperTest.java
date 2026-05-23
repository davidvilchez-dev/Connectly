package com.david.connectly.backend.mapper;

import com.david.connectly.backend.dto.request.RegisterRequest;
import com.david.connectly.backend.dto.response.UserResponse;
import com.david.connectly.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper - Pruebas unitarias")
class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    @DisplayName("toEntity: mapea RegisterRequest a User correctamente")
    void toEntity_shouldMapRegisterRequestToUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@email.com");
        request.setPassword("password123");

        User result = userMapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
        assertThat(result.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("toEntity: retorna null si la petición es null")
    void toEntity_shouldReturnNull_whenRequestIsNull() {
        assertThat(userMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toResponse: mapea User a UserResponse correctamente")
    void toResponse_shouldMapUserToUserResponse() {
        User user = new User();
        user.setId(5L);
        user.setUsername("david");
        user.setEmail("david@email.com");
        user.setBio("My bio");
        user.setAvatarUrl("http://avatar.com/1");

        UserResponse result = userMapper.toResponse(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getUsername()).isEqualTo("david");
        assertThat(result.getEmail()).isEqualTo("david@email.com");
        assertThat(result.getBio()).isEqualTo("My bio");
        assertThat(result.getAvatarUrl()).isEqualTo("http://avatar.com/1");
    }

    @Test
    @DisplayName("toResponse: retorna null si la entidad es null")
    void toResponse_shouldReturnNull_whenEntityIsNull() {
        assertThat(userMapper.toResponse(null)).isNull();
    }
}
