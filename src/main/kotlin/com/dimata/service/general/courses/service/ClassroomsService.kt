package com.dimata.service.general.courses.service

import com.dimata.service.general.courses.model.request.ClassroomsRequestBody
import com.dimata.service.general.courses.model.request.CourseRequestBody
import com.dimata.service.general.courses.model.response.ClassroomsResponse
import com.dimata.service.general.courses.model.response.CourseResponse
import com.dimata.service.general.courses.repository.ClassroomsRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ClassroomsService (
    private val classroomsRepository : ClassroomsRepository
) {
    fun create(body: ClassroomsRequestBody): ClassroomsResponse {
        classroomsRepository.create(body)
        return getAll().last()
    }

    fun getAll(): List<ClassroomsResponse> {
        return classroomsRepository.getAll() // Ambil semua kursus
    }

    fun update(classroomId: String, body: ClassroomsRequestBody): ClassroomsResponse {
        classroomsRepository.update(classroomId, body)
        return getAll().find { it.classroomId == classroomId }!! // Ambil kursus yang diperbarui
    }

    fun delete(classroomId: String) {
        classroomsRepository.delete(classroomId)
    }
}

