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
                        record.get(Tables.CUSTOMER.ID_CUSTOMER),
                        record.get(Tables.CUSTOMER.NAMA),
                        record.get(Tables.CUSTOMER.NO_TELP),
                        record.get(Tables.CUSTOMER.CREATED_AT),
                        record.get(Tables.CUSTOMER.UPDATED_AT),
                        record.get(Tables.CUSTOMER.DELETED_AT)
                ))
                .collect(Collectors.toList());
        return result;
    }

    public Optional<CustomerResponseBody> findByNoTelp(String noTelp) {
        String formattedPhone = convertToInternationalPhone(noTelp);
        return jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.NO_TELP.eq(formattedPhone))
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

        String formattedPhone = convertToInternationalPhone(request.getNoTelp());

        CustomerRecord newCustomer = jooq.newRecord(Tables.CUSTOMER);
        newCustomer.setIdCustomer(customerId);
        newCustomer.setNama(request.getNama());
        newCustomer.setNoTelp(formattedPhone);
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


    // Fungsi gabungan createOrGet tetap dengan logika yang sama
    public CustomerResponseBody createOrGet(CustomerRequestBody request) {
        String formattedPhone = convertToInternationalPhone(request.getNoTelp());
        return findByNoTelp(formattedPhone)
                .orElseGet(() -> {
                    // Pastikan juga set noTelp sudah di format internasional saat create
                    request.setNoTelp(formattedPhone);
                    return create(request);
                });
    }

    private String convertToInternationalPhone(String localPhone) {
        if (localPhone == null) return "";
        localPhone = localPhone.replaceAll("[^0-9]", "");

        if (localPhone.startsWith("0")) {
            return "62" + localPhone.substring(1);
        }
        return localPhone;
    }


}
