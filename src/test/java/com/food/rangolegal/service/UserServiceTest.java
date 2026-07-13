package com.food.rangolegal.service;

import com.food.rangolegal.model.Client;
import com.food.rangolegal.model.User;
import com.food.rangolegal.model.UserType;
import com.food.rangolegal.repository.UserRepository;
import com.food.rangolegal.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserTypeRepository userTypeRepository = mock(UserTypeRepository.class);
    private final UserService service = new UserService(userRepository, userTypeRepository, new BCryptPasswordEncoder());

    @Test
    void updateUserTypeAssociatesExistingUserAndType() {
        User user = new Client();
        user.setId(1L);

        UserType userType = new UserType();
        userType.setId(2L);
        userType.setName("Cliente");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userTypeRepository.findById(2L)).thenReturn(Optional.of(userType));
        when(userRepository.save(user)).thenReturn(user);

        User result = service.updateUserType(1L, 2L);

        assertEquals(2L, result.getUserType().getId());
        assertEquals("Cliente", result.getUserType().getName());
    }

    @Test
    void updateUserTypeThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateUserType(1L, 2L));
    }

    @Test
    void updateUserTypeThrowsWhenTypeDoesNotExist() {
        User user = new Client();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userTypeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateUserType(1L, 2L));
    }
}
