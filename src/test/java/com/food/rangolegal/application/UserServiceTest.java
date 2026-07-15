package com.food.rangolegal.application;

import com.food.rangolegal.application.service.UserService;
import com.food.rangolegal.domain.model.Client;
import com.food.rangolegal.domain.model.User;
import com.food.rangolegal.domain.model.UserType;
import com.food.rangolegal.infrastructure.repository.UserRepository;
import com.food.rangolegal.infrastructure.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserTypeRepository userTypeRepository = mock(UserTypeRepository.class);
    private final UserService service = new UserService(userRepository, userTypeRepository, new BCryptPasswordEncoder());

    @Test
    void updateUserType_whenUserAndTypeExist_associatesAndReturnsUser() {
        User user = new Client();
        user.setId(1L);

        UserType userType = new UserType();
        userType.setId(2L);
        userType.setName("Dono de Restaurante");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userTypeRepository.findById(2L)).thenReturn(Optional.of(userType));
        when(userRepository.save(user)).thenReturn(user);

        User result = service.updateUserType(1L, 2L);

        assertEquals(2L, result.getUserType().getId());
        assertEquals("Dono de Restaurante", result.getUserType().getName());
    }

    @Test
    void updateUserType_whenUserDoesNotExist_throwsRuntimeException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateUserType(99L, 2L));
        verifyNoInteractions(userTypeRepository);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserType_whenUserTypeDoesNotExist_throwsRuntimeException() {
        User user = new Client();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateUserType(1L, 99L));
        verify(userRepository, never()).save(any(User.class));
    }
}
