package com.haylaundry.service.backend.modules.report.service;

import com.haylaundry.service.backend.modules.report.models.dailyincome.request.DailyIncomeRequest;
import com.haylaundry.service.backend.modules.report.models.dailyincome.response.DailyIncomeResponse;
import com.haylaundry.service.backend.modules.report.repository.DailyIncomeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationScoped
public class DailyIncomeService {

    private final DailyIncomeRepository dailyIncomeRepository;

    @Inject
    public DailyIncomeService(DailyIncomeRepository dailyIncomeRepository) {
        this.dailyIncomeRepository = dailyIncomeRepository;
    }

    public void createLaporan(LocalDate tglReport) {
        dailyIncomeRepository.createLaporan(tglReport);
    }

    public DailyIncomeResponse getLaporanByDate(String dateString) {
        return dailyIncomeRepository.getLaporanByDate(dateString);
    }
}
