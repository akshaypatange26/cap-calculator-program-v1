package com.calculator.api.repository;

import com.calculator.api.entity.CalculationErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculationErrorLogRepository extends JpaRepository<CalculationErrorLog, Long> {
}
