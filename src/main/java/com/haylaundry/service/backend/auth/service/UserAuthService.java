package com.haylaundry.service.backend.auth.service;

import com.haylaundry.service.backend.auth.dto.request.UserAuthRequest;
import com.haylaundry.service.backend.auth.dto.response.UserAuthResponse;
import com.haylaundry.service.backend.auth.repository.UserAuthRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;

    public UserAuthService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    public List<UserAuthResponse> getAllUsers() {
        return userAuthRepository.getAll();
    }

    public UserAuthResponse create(UserAuthRequest body) {
        userAuthRepository.create(body);
        return userAuthRepository.getAll().stream()
                .filter(user -> user.getUsername().equals(body.getUsername()) && user.getRole().equals(body.getRole()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gagal membuat user"));
    }

    public UserAuthResponse update(String userId, UserAuthRequest body) {
        int updatedRows = userAuthRepository.update(userId, body);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("User tidak ditemukan atau gagal memperbarui data.");
        }
        return userAuthRepository.getAll().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan setelah update"));
    }

    // Metode untuk menghapus user
    public void delete(String userId) {
        int deletedRows = userAuthRepository.delete(userId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("User tidak ditemukan atau gagal menghapus data.");
        }
    }

    public UserAuthResponse login(String username, String password) {
        return userAuthRepository.getAll().stream()
                .filter(user -> user.getUsername().equals(username)
                        && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Username atau password salah"));
    }

}
