package com.foodtracker.shared.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeServiceImpl implements TimeService {
    @Override
    public LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }
}
