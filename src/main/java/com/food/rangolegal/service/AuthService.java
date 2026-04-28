package com.food.rangolegal.service;

import com.food.rangolegal.dto.LoginRequestDTO;
import com.food.rangolegal.repository.UserRepository;
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
