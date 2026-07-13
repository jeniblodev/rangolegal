package com.food.rangolegal.service;

import com.food.rangolegal.dto.UserTypeRequestDTO;
import com.food.rangolegal.model.UserType;
import com.food.rangolegal.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserTypeServiceTest {

    private final UserTypeRepository repository = mock(UserTypeRepository.class);
    private final UserTypeService service = new UserTypeService(repository);

    @Test
    void createSavesUserTypeWithName() {
        UserType saved = new UserType();
        saved.setId(1L);
        saved.setName("Cliente");

        when(repository.save(org.mockito.ArgumentMatchers.any(UserType.class))).thenReturn(saved);

        UserType result = service.create(new UserTypeRequestDTO("Cliente"));

        assertEquals(1L, result.getId());
        assertEquals("Cliente", result.getName());
    }

    @Test
    void getByIdThrowsWhenUserTypeDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getById(99L));
    }

    @Test
    void updateChangesUserTypeName() {
        UserType existing = new UserType();
        existing.setId(1L);
        existing.setName("Cliente");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        UserType result = service.update(1L, new UserTypeRequestDTO("Dono de Restaurante"));

        assertEquals("Dono de Restaurante", result.getName());
        verify(repository).save(existing);
    }
}
