package com.food.rangolegal.service;

import com.food.rangolegal.dto.UserTypeRequestDTO;
import com.food.rangolegal.model.UserType;
import com.food.rangolegal.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserTypeServiceTest {

    private final UserTypeRepository repository = mock(UserTypeRepository.class);
    private final UserTypeService service = new UserTypeService(repository);

    @Test
    void create_whenDtoIsValid_returnsSavedUserType() {
        UserType saved = new UserType();
        saved.setId(1L);
        saved.setName("Dono de Restaurante");

        when(repository.save(any(UserType.class))).thenReturn(saved);

        UserType result = service.create(new UserTypeRequestDTO("Dono de Restaurante"));

        assertEquals(1L, result.getId());
        assertEquals("Dono de Restaurante", result.getName());
    }

    @Test
    void listAll_whenTypesExist_returnsAllTypes() {
        UserType client = new UserType();
        client.setId(1L);
        client.setName("Cliente");

        UserType owner = new UserType();
        owner.setId(2L);
        owner.setName("Dono de Restaurante");

        List<UserType> userTypes = List.of(client, owner);
        when(repository.findAll()).thenReturn(userTypes);

        List<UserType> result = service.listAll();

        assertEquals(userTypes, result);
    }

    @Test
    void getById_whenExists_returnsUserType() {
        UserType userType = new UserType();
        userType.setId(1L);
        userType.setName("Cliente");

        when(repository.findById(1L)).thenReturn(Optional.of(userType));

        UserType result = service.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Cliente", result.getName());
    }

    @Test
    void getById_whenMissing_throwsRuntimeException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getById(99L));
    }

    @Test
    void update_whenExists_updatesNameAndReturnsSavedUserType() {
        UserType existing = new UserType();
        existing.setId(1L);
        existing.setName("Cliente");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        UserType result = service.update(1L, new UserTypeRequestDTO("Dono de Restaurante"));

        assertEquals(1L, result.getId());
        assertEquals("Dono de Restaurante", result.getName());
    }

    @Test
    void update_whenMissing_throwsRuntimeException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(99L, new UserTypeRequestDTO("Cliente")));
        verify(repository, never()).save(any(UserType.class));
    }

    @Test
    void delete_whenExists_deletesUserType() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_whenMissing_throwsRuntimeException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(99L);
    }
}
