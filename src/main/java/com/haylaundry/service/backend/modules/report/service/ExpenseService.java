package com.haylaundry.service.backend.modules.report.service;

import com.haylaundry.service.backend.modules.report.models.expense.request.ExpenseRequest;
import com.haylaundry.service.backend.modules.report.models.expense.response.ExpenseResponse;
import com.haylaundry.service.backend.modules.report.repository.ExpenseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ExpenseService {
    @Inject
    private ExpenseRepository expenseRepository;

    public List<ExpenseResponse> getExpense() {
        return expenseRepository.getAll();
    }

    public ExpenseResponse create(ExpenseRequest body) {
        return expenseRepository.createExpense(body);
    }
}
