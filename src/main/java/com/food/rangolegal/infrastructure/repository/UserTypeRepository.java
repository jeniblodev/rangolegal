package com.food.rangolegal.infrastructure.repository;

import com.food.rangolegal.domain.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
}
