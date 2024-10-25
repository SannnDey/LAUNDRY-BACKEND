package com.dimata.service.general.courses.service

import com.dimata.service.general.courses.model.request.TeacherRequestBody
import com.dimata.service.general.courses.model.response.TeacherResponse
import com.dimata.service.general.courses.repository.TeacherRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TeacherService(
    private val teacherRepository: TeacherRepository
) {
    fun create(body: TeacherRequestBody): TeacherResponse {
        val data = teacherRepository.create(body)
        return TeacherResponse(
            teacherId = data.teacherId,
            name = data.name,
            identityId = data.identityId
        )
    }

    fun update(teacherId: String, body: TeacherRequestBody): TeacherResponse {
        teacherRepository.update(teacherId, body)
        return TeacherResponse(
            teacherId = teacherId,
            name = body.name,
            identityId = body.identityId
        )
    }

    fun delete(teacherId: String) {
        teacherRepository.delete(teacherId)
    }
}
