package com.haylaundry.service.backend.modules.auth.service;

import com.haylaundry.service.backend.core.utils.PasswordService;
import com.haylaundry.service.backend.modules.auth.models.request.UserAuthRequest;
import com.haylaundry.service.backend.modules.auth.models.response.UserAuthResponse;
import com.haylaundry.service.backend.modules.auth.repository.UserAuthRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class UserAuthService {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);

    private final UserAuthRepository userAuthRepository;

    @Inject
    private PasswordService passwordService;

    public UserAuthService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    // Mengambil semua pengguna
    public List<UserAuthResponse> getAllUsers() {
        logger.info("Mengambil data semua pengguna dari repository");
        return userAuthRepository.getAll();
    }

    // Registrasi pengguna baru dengan hashing password
    public UserAuthResponse create(UserAuthRequest body) {
        logger.info("Mendaftarkan pengguna baru: {}", body.getUsername());

        // Hash the password before storing it
        String hashedPassword = passwordService.hashPassword(body.getPassword());
        body.setPassword(hashedPassword);

        userAuthRepository.create(body);
        return userAuthRepository.getAll().stream()
                .filter(user -> user.getUsername().equals(body.getUsername()) && user.getRole().equals(body.getRole()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gagal membuat user"));
    }

    // Login pengguna dan verifikasi password
    public UserAuthResponse login(String username, String password) {
        logger.info("Proses login untuk pengguna: {}", username);

        // Get user from database (simulating the fetch)
        UserAuthResponse user = userAuthRepository.getAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Username atau password salah"));

        // Verify the password with the stored hash
        if (!passwordService.verifyPassword(password, user.getPassword())) {
            throw new IllegalArgumentException("Username atau password salah");
        }

        return user;
    }

    // Menghasilkan token JWT
    public String generateJwtToken(UserAuthResponse user) {
        logger.info("Menghasilkan JWT untuk pengguna: {}", user.getUsername());
        return Jwt.issuer("user-service")
                .upn(user.getUsername())
                .groups(user.getRole().toLowerCase())
                .audience("user-service")
                .expiresAt(Instant.now().plusSeconds(3600))
                .sign();
    }

    // Update data pengguna
    public UserAuthResponse update(String userId, UserAuthRequest body) {
        logger.info("Memperbarui data pengguna dengan ID: {}", userId);
        int updatedRows = userAuthRepository.update(userId, body);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("User tidak ditemukan atau gagal memperbarui data.");
        }
        return userAuthRepository.getAll().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan setelah update"));
    }

    // Hapus pengguna
    public void delete(String userId) {
        logger.info("Menghapus pengguna dengan ID: {}", userId);
        int deletedRows = userAuthRepository.delete(userId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("User tidak ditemukan atau gagal menghapus data.");
        }
    }
}
