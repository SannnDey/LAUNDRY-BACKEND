package com.dimata.service.general.courses.service

import com.dimata.service.general.courses.model.request.CourseRequestBody
import com.dimata.service.general.courses.model.response.CourseResponse
import com.dimata.service.general.courses.repository.CourseRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CourseService(
    private val courseRepository: CourseRepository
) {
    fun create(body: CourseRequestBody): CourseResponse {
        courseRepository.create(body)
        // Mengambil data kursus setelah disimpan
        return getAll().last() // Ambil kursus terakhir yang ditambahkan
    }

    fun getAll(): List<CourseResponse> {
        return courseRepository.getAll() // Ambil semua kursus
    }

    fun update(courseId: String, body: CourseRequestBody): CourseResponse {
        courseRepository.update(courseId, body)
        return getAll().find { it.courseId == courseId }!! // Ambil kursus yang diperbarui
    }

    fun delete(courseId: String) {
        courseRepository.delete(courseId)
    }
}
