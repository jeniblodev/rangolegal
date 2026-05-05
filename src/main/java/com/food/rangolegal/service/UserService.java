package com.food.rangolegal.service;

import com.food.rangolegal.dto.PasswordUpdateDTO;
import com.food.rangolegal.dto.UserRequestDTO;
import com.food.rangolegal.dto.UserUpdateDTO;
import com.food.rangolegal.model.Client;
import com.food.rangolegal.model.RestaurantOwner;
import com.food.rangolegal.model.User;
import com.food.rangolegal.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        User user = "RESTAURANT_OWNER".equalsIgnoreCase(dto.userType()) ? new RestaurantOwner() : new Client();

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setLogin(dto.login());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setAddress(dto.address());

        return userRepository.save(user);
    }

    public List<User> findByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    public User updateData(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não identificado"));

        user.setName(userUpdateDTO.name());
        user.setLogin(userUpdateDTO.login());
        user.setAddress(userUpdateDTO.address());

        return userRepository.save(user);
    }

    public void updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não localizado"));

        if (!passwordEncoder.matches(passwordUpdateDTO.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.newPassword()));
        userRepository.save(user);
    }
        @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
