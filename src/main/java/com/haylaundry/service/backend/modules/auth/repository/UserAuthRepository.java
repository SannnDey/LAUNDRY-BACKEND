package com.haylaundry.service.backend.modules.auth.repository;

import com.haylaundry.service.backend.modules.auth.models.request.UserAuthRequest;
import com.haylaundry.service.backend.modules.auth.models.response.UserAuthResponse;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.util.List;

@ApplicationScoped
public class UserAuthRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    public List<UserAuthResponse> getAll() {
        return jooq.selectFrom(Tables.USER)
                .fetchInto(UserAuthResponse.class);
    }

    public void create(UserAuthRequest body) {
        if (isUserExists(body.getUsername(), body.getRole())) {
            throw new IllegalArgumentException("Username dengan role tersebut sudah terdaftar.");
        }

        var record = jooq.newRecord(Tables.USER);
        record.setUserId(UuidCreator.getTimeOrderedEpoch().toString());
        record.setUsername(body.getUsername());
        record.setRole(body.getRole());
        record.setPassword(body.getPassword());
        record.store();
    }


    public int update(String userId, UserAuthRequest body) {
        return jooq.update(Tables.USER)
                .set(Tables.USER.USERNAME, body.getUsername())
                .set(Tables.USER.PASSWORD, body.getPassword())
                .where(Tables.USER.USER_ID.eq(userId))
                .execute();
    }

    public int delete(String userId) {
        return jooq.delete(Tables.USER)
                .where(Tables.USER.USER_ID.eq(userId))
                .execute();
    }

    public boolean isUserExists(String username, String role) {
        return jooq.fetchExists(jooq.selectFrom(Tables.USER)
                .where(Tables.USER.USERNAME.eq(username))
                .and(Tables.USER.ROLE.eq(role)));
    }

//    public UserAuthResponse getUserByEmail(String email) {
//        return jooq.selectFrom(Tables.USER)
//                .where(Tables.USER.ROLE.eq(email))
//                .fetchOneInto(UserAuthResponse.class);
//    }
//
//
//    public UserAuthResponse getUserByUsernameAndRole(String username, String role) {
//        return jooq.selectFrom(Tables.USER)
//                .where(Tables.USER.USERNAME.eq(username))
//                .and(Tables.USER.ROLE.eq(role))
//                .fetchOneInto(UserAuthResponse.class);
//    }

}
