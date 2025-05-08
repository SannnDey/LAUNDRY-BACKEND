package com.haylaundry.service.backend.core.orm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

@ApplicationScoped
public class JooqRepository {

    @Inject
    protected DSLContext jooq;

}
