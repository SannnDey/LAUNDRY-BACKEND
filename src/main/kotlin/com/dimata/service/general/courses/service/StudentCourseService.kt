package com.dimata.service.general.courses.service

import com.dimata.service.general.courses.model.request.StudentCourseRequestBody
import com.dimata.service.general.courses.model.response.StudentCourseResponse
import com.dimata.service.general.courses.repository.StudentCourseRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StudentCourseService(
    private val studentCourseRepository: StudentCourseRepository
) {
    fun getAll(): List<StudentCourseResponse> {
        return studentCourseRepository.getAll()
    }

    fun create(body: StudentCourseRequestBody): StudentCourseResponse {
        studentCourseRepository.create(body)
        return studentCourseRepository.getAll().last() // Mengambil data terakhir yang ditambahkan
    }



    fun delete(studentId: String, courseId: String) {
        studentCourseRepository.delete(studentId, courseId)
    }
}
