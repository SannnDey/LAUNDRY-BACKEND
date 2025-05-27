package com.haylaundry.service.backend.modules.report.models.finance.request;

import java.time.LocalDate;

public class FinanceRequest {
    private String tglReport;

    public String getTglReport() {
        return tglReport;
    }

    public void setTglReport(String tglReport) {
        this.tglReport = tglReport;
    }


}
