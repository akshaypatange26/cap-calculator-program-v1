package com.calculator.api.repository;

import com.calculator.api.entity.CalculatorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<CalculatorUser, Long> {
    Optional<CalculatorUser> findByUsername(String username);
}
