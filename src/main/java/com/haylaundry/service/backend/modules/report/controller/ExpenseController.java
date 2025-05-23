package com.haylaundry.service.backend.modules.report.controller;

import com.haylaundry.service.backend.modules.report.models.expense.request.ExpenseRequest;
import com.haylaundry.service.backend.modules.report.models.expense.response.ExpenseResponse;
import com.haylaundry.service.backend.modules.report.service.ExpenseService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Logger;

@Path("api/expense")
public class ExpenseController {

    private static final Logger LOG = Logger.getLogger(ExpenseController.class.getName());

    @Inject
    ExpenseService expenseService;

    @GET
    public Response getAllExpenses() {
        List<ExpenseResponse> expenses = expenseService.getExpense();
        return Response.ok(expenses).build();
    }

    @POST
    public Response createExpense(ExpenseRequest request) {
        LOG.info("Received request to create a new expense: " + request);  // Log the incoming request

        try {
            ExpenseResponse newExpense = expenseService.create(request);
            LOG.info("Expense created successfully with ID: " + newExpense.getIdPengeluaran());  // Log the success
            return Response.status(Response.Status.CREATED).entity(newExpense).build();
        } catch (Exception e) {
            LOG.severe("Error creating expense: " + e.getMessage());  // Log the error
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create expense: " + e.getMessage())
                    .build();
        }
    }
}
