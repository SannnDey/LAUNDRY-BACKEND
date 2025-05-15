package com.haylaundry.service.backend.modules.auth.repository;

import com.haylaundry.service.backend.modules.auth.models.request.UserAuthRequest;
import com.haylaundry.service.backend.modules.auth.models.response.UserAuthResponse;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class UserAuthRepository extends JooqRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthRepository.class);

    @Inject
    private DSLContext jooq;

    public List<UserAuthResponse> getAll() {
        logger.info("Mengambil data semua pengguna dari database");
        return jooq.selectFrom(Tables.USER)
                .fetchInto(UserAuthResponse.class);
    }

    public void create(UserAuthRequest body) {
        if (isUserExists(body.getUsername(), body.getRole())) {
            logger.warn("Pengguna dengan username {} dan role {} sudah ada", body.getUsername(), body.getRole());
            throw new IllegalArgumentException("Username dengan role tersebut sudah terdaftar.");
        }

        var record = jooq.newRecord(Tables.USER);
        record.setUserId(UuidCreator.getTimeOrderedEpoch().toString());
        record.setUsername(body.getUsername());
        record.setRole(body.getRole());
        record.setPassword(body.getPassword());
        record.store();

        logger.info("Pengguna baru dengan username {} berhasil ditambahkan", body.getUsername());
    }

    public int update(String userId, UserAuthRequest body) {
        logger.info("Memperbarui pengguna dengan ID: {}", userId);
        return jooq.update(Tables.USER)
                .set(Tables.USER.USERNAME, body.getUsername())
                .set(Tables.USER.PASSWORD, body.getPassword())
                .where(Tables.USER.USER_ID.eq(userId))
                .execute();
    }

    public int delete(String userId) {
        logger.info("Menghapus pengguna dengan ID: {}", userId);
        return jooq.delete(Tables.USER)
                .where(Tables.USER.USER_ID.eq(userId))
                .execute();
    }

    public boolean isUserExists(String username, String role) {
        return jooq.fetchExists(jooq.selectFrom(Tables.USER)
                .where(Tables.USER.USERNAME.eq(username))
                .and(Tables.USER.ROLE.eq(role)));
    }
}
