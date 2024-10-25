package com.dimata.service.general.courses.service

import com.dimata.service.general.courses.model.request.StudentRequestBody
import com.dimata.service.general.courses.model.response.StudentResponse
import com.dimata.service.general.courses.repository.StudentRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StudentService(
    private val studentRepository: StudentRepository
) {
    fun create(body: StudentRequestBody): StudentResponse {
        val data = studentRepository.create(body)
        return StudentResponse(
            studentId = data.studentId,
            name = data.name,
            identityId = data.identityId // Ditambahkan untuk respons
        )
    }

    fun update(studentId: String, body: StudentRequestBody): StudentResponse {
        studentRepository.update(studentId, body)
        return StudentResponse(
            studentId = studentId,
            name = body.name,
            identityId = body.identityId // Ditambahkan untuk respons
        )
    }

    fun delete(studentId: String) {
        studentRepository.delete(studentId)
    }
}
