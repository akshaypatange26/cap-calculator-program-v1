package com.calculator.api.service;

import com.calculator.api.repository.CalculationErrorLogRepository;
import com.calculator.api.repository.CalculationHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Background task service to automatically clean up database log entries
 * that are older than 24 hours (1 day).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupService {

    private final CalculationHistoryRepository historyRepository;
    private final CalculationErrorLogRepository errorLogRepository;

    /**
     * Trigger a cleanup sweep immediately when the application starts up.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready. Triggering initial database log cleanup sweep...");
        pruneOldLogs();
    }

    /**
     * Deletes history records and error logs older than 24 hours.
     * Scheduled to run automatically every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void pruneOldLogs() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        log.info("Starting background database log cleanup. Threshold: {}", threshold);

        try {
            int deletedHistory = historyRepository.deleteByCreatedAtBefore(threshold);
            int deletedErrors = errorLogRepository.deleteByCreatedAtBefore(threshold);

            log.info("Database cleanup complete. Deleted history records: {}, Deleted error logs: {}",
                    deletedHistory, deletedErrors);
        } catch (Exception e) {
            log.error("Failed to execute background database log cleanup: {}", e.getMessage(), e);
        }
    }
}
