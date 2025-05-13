package com.haylaundry.service.backend.modules.customer.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.records.CustomerRecord;
import com.haylaundry.service.backend.modules.customer.models.request.CustomerRequestBody;
import com.haylaundry.service.backend.modules.customer.models.response.CustomerResponseBody;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    // GET All Customers - customerId ditampilkan
    // ✅ Ambil semua data customer
    public List<CustomerResponseBody> getAll() {
        List<CustomerResponseBody> result = jooq.selectFrom(Tables.CUSTOMER)
                .fetch()
                .stream()
                .map(record -> new CustomerResponseBody(
                        record.get(Tables.CUSTOMER.ID_CUSTOMER),   // ini ambil dari kolom id_customer
                        record.get(Tables.CUSTOMER.NAMA),
                        record.get(Tables.CUSTOMER.NO_TELP),
                        record.get(Tables.CUSTOMER.CREATED_AT),
                        record.get(Tables.CUSTOMER.UPDATED_AT),
                        record.get(Tables.CUSTOMER.DELETED_AT)
                ))
                .collect(Collectors.toList());
        return result;
    }

    // GET Customer by noTelp - customerId ditampilkan
    public Optional<CustomerResponseBody> findByNoTelp(String noTelp) {
        return jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.NO_TELP.eq(noTelp))
                .fetchOptional()
                .map(record -> new CustomerResponseBody(
                        record.get(Tables.CUSTOMER.ID_CUSTOMER),
                        record.get(Tables.CUSTOMER.NAMA),
                        record.get(Tables.CUSTOMER.NO_TELP),
                        record.get(Tables.CUSTOMER.CREATED_AT),
                        record.get(Tables.CUSTOMER.UPDATED_AT),
                        record.get(Tables.CUSTOMER.DELETED_AT)
                ));
    }


    // POST Create Customer - customerId diset ke null pada response
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

        // Mengembalikan customer baru dengan customerId diset ke null
        return new CustomerResponseBody(
                newCustomer.getIdCustomer(),
                newCustomer.getNama(),
                newCustomer.getNoTelp(),
                newCustomer.getCreatedAt(),
                newCustomer.getUpdatedAt(),
                newCustomer.getDeletedAt()
        );
    }

    // Fungsi gabungan createOrGet tetap dengan logika yang sama
    public CustomerResponseBody createOrGet(CustomerRequestBody request) {
        return findByNoTelp(request.getNoTelp())
                .orElseGet(() -> create(request));
    }
}
