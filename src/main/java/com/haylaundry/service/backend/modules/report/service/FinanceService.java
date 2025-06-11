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

    public FinanceResponse getLaporanKeuanganByMonth(String date) {
        return financeRepository.getLaporanByMonth(date);
    }

    public FinanceDateResponse getReportByDateRange(String startDate, String endDate) {
        return financeRepository.getLaporanByDateRange(startDate, endDate);
    }
}
