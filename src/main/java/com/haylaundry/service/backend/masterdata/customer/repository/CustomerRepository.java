package com.haylaundry.service.backend.masterdata.customer.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.records.CustomerRecord;
import com.haylaundry.service.backend.masterdata.customer.models.request.CustomerRequestBody;
import com.haylaundry.service.backend.masterdata.customer.models.response.CustomerResponseBody;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    public List<CustomerResponseBody> getAll() {
        return jooq.selectFrom(Tables.CUSTOMER)
                .fetchInto(CustomerResponseBody.class);
    }
    // ✅ Cari customer berdasarkan no_telp
    public Optional<CustomerResponseBody> findByNoTelp(String noTelp) {
        return jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.NO_TELP.eq(noTelp))
                .fetchOptionalInto(CustomerResponseBody.class);
    }

    // ✅ Buat customer baru
    public CustomerResponseBody create(CustomerRequestBody request) {
        String customerId = UuidCreator.getTimeOrderedEpoch().toString();
        LocalDateTime now = LocalDateTime.now();

        CustomerRecord newCustomer = jooq.newRecord(Tables.CUSTOMER);
        newCustomer.setIdCustomer(customerId);
        newCustomer.setNama(request.getNama());
        newCustomer.setNoTelp(request.getNoTelp());
        newCustomer.setCreatedAt(now);
        newCustomer.setUpdatedAt(now);
        newCustomer.store();

        return new CustomerResponseBody(
                newCustomer.getIdCustomer(),
                newCustomer.getNama(),
                newCustomer.getNoTelp(),
                newCustomer.getCreatedAt(),
                newCustomer.getUpdatedAt(),
                newCustomer.getDeletedAt()
        );
    }

    // ✅ Fungsi gabungan: cek jika ada ambil, jika tidak buat
    // ✅ Fungsi gabungan: cek jika ada ambil, jika tidak buat
    public CustomerResponseBody createOrGet(CustomerRequestBody request) {
        Optional<CustomerResponseBody> existing = findByNoTelp(request.getNoTelp());
        if (existing.isPresent()) {
            // Jika customer sudah ada, kembalikan customer yang ada
            return existing.get();
        } else {
            // Jika customer belum ada, buat customer baru
            return create(request);
        }
    }

}
