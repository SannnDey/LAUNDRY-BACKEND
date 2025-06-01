package com.haylaundry.service.backend.modules.report.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.records.PengeluaranRecord;
import com.haylaundry.service.backend.modules.report.models.expense.request.ExpenseRequest;
import com.haylaundry.service.backend.modules.report.models.expense.response.ExpenseResponse;
import com.haylaundry.service.backend.modules.report.service.DailyIncomeService;
import java.text.DecimalFormat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExpenseRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    @Inject
    private DailyIncomeService dailyIncomeService;


    public List<ExpenseResponse> getAll() {
        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Ambil semua data pengeluaran dan format nominal
        List<ExpenseResponse> result = jooq.selectFrom(Tables.PENGELUARAN)
                .fetch()
                .stream()
                .map(record -> new ExpenseResponse(
                        record.get(Tables.PENGELUARAN.ID_PENGELUARAN),
                        record.get(Tables.PENGELUARAN.JENIS_PENGELUARAN),
                        formatter.format(record.get(Tables.PENGELUARAN.NOMINAL)),  // Format nominal dengan pemisah ribuan
                        record.get(Tables.PENGELUARAN.CATATAN),
                        record.get(Tables.PENGELUARAN.TGL_PENGELUARAN)
                ))
                .collect(Collectors.toList());
        return result;
    }

    // New method to get expenses by date with format DDDD-MMMM-YYYY
    public List<ExpenseResponse> getByDate(String dateString) {
        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Format tanggal dan tentukan locale Indonesia
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("id-ID"));

        // Parse input date string ke objek LocalDate
        LocalDate date = LocalDate.parse(dateString, formatterDate);

        // Ambil pengeluaran berdasarkan tanggal yang diberikan dan format nominal
        List<ExpenseResponse> result = jooq.selectFrom(Tables.PENGELUARAN)
                .where(Tables.PENGELUARAN.TGL_PENGELUARAN.cast(java.sql.Date.class).eq(java.sql.Date.valueOf(date))) // Bandingkan hanya tanggal
                .fetch()
                .stream()
                .map(record -> new ExpenseResponse(
                        record.get(Tables.PENGELUARAN.ID_PENGELUARAN),
                        record.get(Tables.PENGELUARAN.JENIS_PENGELUARAN),
                        formatter.format(record.get(Tables.PENGELUARAN.NOMINAL)),  // Format nominal dengan pemisah ribuan
                        record.get(Tables.PENGELUARAN.CATATAN),
                        record.get(Tables.PENGELUARAN.TGL_PENGELUARAN)
                ))
                .collect(Collectors.toList());
        return result;
    }


    public ExpenseResponse createExpense(ExpenseRequest request) {
        String idPengeluaran = UuidCreator.getTimeOrderedEpoch().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // Membuat record pengeluaran baru
        PengeluaranRecord newPengeluaran = jooq.newRecord(Tables.PENGELUARAN);
        newPengeluaran.setIdPengeluaran(idPengeluaran);
        newPengeluaran.setJenisPengeluaran(request.getJenisPengeluaran());
        newPengeluaran.setNominal(request.getNominal());
        newPengeluaran.setCatatan(request.getCatatan());
        newPengeluaran.setTglPengeluaran(now);
        newPengeluaran.store();

        // Update laporan harian setelah pengeluaran baru tercatat
        dailyIncomeService.createLaporan(today);

        // Format nominal dengan pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedNominal = formatter.format(request.getNominal());  // Format nominal dengan pemisah ribuan

        // Mengembalikan response dengan nominal yang sudah diformat
        return new ExpenseResponse(
                newPengeluaran.getIdPengeluaran(),
                newPengeluaran.getJenisPengeluaran(),
                formattedNominal,  // Menggunakan formatted nominal
                newPengeluaran.getCatatan(),
                newPengeluaran.getTglPengeluaran()
        );
    }
}
