package com.haylaundry.service.backend.masterdata.customer.service;

import com.haylaundry.service.backend.masterdata.customer.models.request.CustomerRequestBody;
import com.haylaundry.service.backend.masterdata.customer.models.response.CustomerResponseBody;
import com.haylaundry.service.backend.masterdata.customer.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerResponseBody> getAllCustomers() {
        return customerRepository.getAll();
    }

    @Transactional
    public CustomerResponseBody create(CustomerRequestBody body) {
        return customerRepository.create(body); // langsung return dari repository
    }

    public CustomerResponseBody findByNoTelp(String noTelp) {
        return customerRepository.findByNoTelp(noTelp)
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));
    }

    // Menambahkan metode createOrGet di sini
    public CustomerResponseBody createOrGet(CustomerRequestBody request) {
        return customerRepository.createOrGet(request); // Tidak perlu perubahan
    }
}
