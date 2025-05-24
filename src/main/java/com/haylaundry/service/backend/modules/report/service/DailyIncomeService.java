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

    // Constructor injection
    @Inject
    public DailyIncomeService(DailyIncomeRepository dailyIncomeRepository) {
        this.dailyIncomeRepository = dailyIncomeRepository;
    }

    // Metode untuk membuat laporan pemasukan harian berdasarkan LocalDateTime
    public void createLaporan(LocalDate tglReport) {
        // Memanggil repository untuk membuat laporan dengan tanggal yang diberikan
        dailyIncomeRepository.createLaporan(tglReport);
    }

    // Method to retrieve the daily income report based on the date
    public DailyIncomeResponse getLaporanByDate(String dateString) {
        // Directly call the repository method to fetch the report
        return dailyIncomeRepository.getLaporanByDate(dateString);  // Calls the repository to fetch the report
    }
}
