package com.foodtracker.dashboard.usecase.dau;

import com.foodtracker.dashboard.dto.DauMetricsDto;
import com.foodtracker.shared.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateDauMetricsUseCaseImpl implements CalculateDauMetricsUseCase {

    private final EventRepository eventRepository;

    @Override
    public DauMetricsDto calculateDauMetrics() {
        Instant tenSecondsAgo = Instant.now().minusSeconds(10);
        Long dauCount = eventRepository.countDistinctUsersSince(tenSecondsAgo);

        return DauMetricsDto.builder()
                .dauCount(dauCount)
                .timestamp(LocalDateTime.now())  // Keep LocalDateTime for DTO response format
                .period("last_10_seconds")
                .build();
    }
}