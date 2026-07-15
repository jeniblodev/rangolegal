package com.food.rangolegal.application.service;

import com.food.rangolegal.application.dto.UserTypeRequestDTO;
import com.food.rangolegal.domain.model.UserType;
import com.food.rangolegal.infrastructure.repository.UserTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserTypeService {

    private final UserTypeRepository repository;

    public UserTypeService(UserTypeRepository repository) {
        this.repository = repository;
    }

    public UserType create(UserTypeRequestDTO dto) {
        UserType userType = new UserType();
        userType.setName(dto.name());
        return repository.save(userType);
    }

    public List<UserType> listAll() {
        return repository.findAll();
    }

    public UserType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de usuario nao encontrado com o ID: " + id));
    }

    @Transactional
    public UserType update(Long id, UserTypeRequestDTO dto) {
        UserType userType = getById(id);
        userType.setName(dto.name());
        return repository.save(userType);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Tipo de usuario nao encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}
