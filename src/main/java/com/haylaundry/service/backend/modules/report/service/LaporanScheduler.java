package com.haylaundry.service.backend.modules.report.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.logging.Logger;

@ApplicationScoped
public class LaporanScheduler {

    private static final Logger LOG = Logger.getLogger(LaporanScheduler.class.getName());

    @Inject
    private DailyIncomeService dailyIncomeService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateDailyReport() {
        LocalDate today = LocalDate.now();

        LOG.info("Generating daily report for: " + today);

        dailyIncomeService.createLaporan(today);
    }
}
