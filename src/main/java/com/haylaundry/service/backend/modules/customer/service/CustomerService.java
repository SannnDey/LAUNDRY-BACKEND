package com.haylaundry.service.backend.modules.customer.service;

import com.haylaundry.service.backend.modules.customer.models.request.CustomerRequestBody;
import com.haylaundry.service.backend.modules.customer.models.response.CustomerResponseBody;
import com.haylaundry.service.backend.modules.customer.repository.CustomerRepository;
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
        return customerRepository.create(body);
    }

    public CustomerResponseBody findByNoTelp(String noTelp) {
        return customerRepository.findByNoTelp(noTelp)
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));
    }

    public CustomerResponseBody createOrGet(CustomerRequestBody request) {
        return customerRepository.createOrGet(request);
    }
}
