package com.haylaundry.service.backend.modules.report.service;

import com.haylaundry.service.backend.jooq.gen.tables.records.LaporanKeuanganRecord;
import com.haylaundry.service.backend.modules.report.models.finance.response.FinanceDateResponse;
import com.haylaundry.service.backend.modules.report.models.finance.response.FinanceResponse;
import com.haylaundry.service.backend.modules.report.repository.FinanceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FinanceService {

    @Inject
    private FinanceRepository financeRepository;

    // Method to get laporan keuangan bulanan by month (bulan dan tahun)
    public FinanceResponse getLaporanKeuanganByMonth(String date) {
        // Memanggil repository untuk mengambil laporan keuangan berdasarkan bulan dan tahun
        return financeRepository.getLaporanByMonth(date);
    }

    // Method to get financial report by date range
    public FinanceDateResponse getReportByDateRange(String startDate, String endDate) {
        return financeRepository.getLaporanByDateRange(startDate, endDate);
    }
}
