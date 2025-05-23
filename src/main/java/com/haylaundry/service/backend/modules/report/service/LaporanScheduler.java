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

    // Penjadwalan untuk menghasilkan laporan setiap pukul 00:00
    @Scheduled(cron = "0 0 0 * * ?") // Setiap pukul 00:00 (tengah malam)
    public void generateDailyReport() {
        // Ambil tanggal sekarang
        LocalDate today = LocalDate.now();  // Menggunakan LocalDate untuk tanggal saat ini

        LOG.info("Generating daily report for: " + today);

        // Panggil DailyIncomeService untuk membuat laporan
        dailyIncomeService.createLaporan(today);
    }
}
