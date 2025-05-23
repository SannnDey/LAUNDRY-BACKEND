package com.haylaundry.service.backend.modules.report.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.records.PengeluaranRecord;
import com.haylaundry.service.backend.modules.report.models.expense.request.ExpenseRequest;
import com.haylaundry.service.backend.modules.report.models.expense.response.ExpenseResponse;
import com.haylaundry.service.backend.modules.report.service.DailyIncomeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExpenseRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    @Inject
    private DailyIncomeService dailyIncomeService;


    public List<ExpenseResponse> getAll() {
        List<ExpenseResponse> result = jooq.selectFrom(Tables.PENGELUARAN)
                .fetch()
                .stream()
                .map(record -> new ExpenseResponse(
                        record.get(Tables.PENGELUARAN.ID_PENGELUARAN),
                        record.get(Tables.PENGELUARAN.JENIS_PENGELUARAN),
                        record.get(Tables.PENGELUARAN.NOMINAL),
                        record.get(Tables.PENGELUARAN.CATATAN),
                        record.get(Tables.PENGELUARAN.TGL_PENGELUARAN),
                        record.get(Tables.PENGELUARAN.UPDATED_AT)
                ))
                .collect(Collectors.toList());
        return result;
    }


    public  ExpenseResponse createExpense(ExpenseRequest request) {
        String idPengeluaran = UuidCreator.getTimeOrderedEpoch().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        PengeluaranRecord newPengeluaran = jooq.newRecord(Tables.PENGELUARAN);
        newPengeluaran.setIdPengeluaran(idPengeluaran);
        newPengeluaran.setJenisPengeluaran(request.getJenisPengeluaran());
        newPengeluaran.setNominal(request.getNominal());
        newPengeluaran.setCatatan(request.getCatatan());
        newPengeluaran.setTglPengeluaran(now);
        newPengeluaran.setUpdatedAt(null);
        newPengeluaran.store();

        // Update laporan harian setelah pengeluaran baru tercatat
        dailyIncomeService.createLaporan(today);

        return new ExpenseResponse(
                newPengeluaran.getIdPengeluaran(),
                newPengeluaran.getJenisPengeluaran(),
                newPengeluaran.getNominal(),
                newPengeluaran.getCatatan(),
                newPengeluaran.getTglPengeluaran(),
                newPengeluaran.getUpdatedAt()
        );
    }
}
