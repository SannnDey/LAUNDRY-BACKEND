package com.haylaundry.service.backend.modules.report.models.dailyincome.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyIncomeRequest {
    private LocalDate tglReport;

    // Getters and Setters
    public LocalDate getTglReport() {
        return tglReport;
    }

    public void setTglReport(LocalDate tglReport) {
        this.tglReport = tglReport;
    }
}
