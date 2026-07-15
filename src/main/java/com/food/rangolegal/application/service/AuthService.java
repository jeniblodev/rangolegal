package com.food.rangolegal.application.service;

import com.food.rangolegal.application.dto.LoginRequestDTO;
import com.food.rangolegal.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean validateLogin(LoginRequestDTO loginRequestDTO) {
        return userRepository.findByLogin(loginRequestDTO.login())
                .map(user -> passwordEncoder.matches(loginRequestDTO.password(), user.getPassword()))
                .orElse(false);
    }
}
