package com.dimata.service.general.courses.repository

import com.dimata.service.general.courses.core.orm.JooqRepository
import com.dimata.service.general.courses.jooq.gen.Tables.STUDENTS
import com.dimata.service.general.courses.model.request.StudentRequestBody
import com.dimata.service.general.courses.model.response.StudentResponse
import com.github.f4b6a3.uuid.UuidCreator
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StudentRepository : JooqRepository() {
    fun getAll(): List<StudentResponse> = jooq.selectFrom(STUDENTS).fetchInto(StudentResponse::class.java)

    fun create(body: StudentRequestBody) = jooq.newRecord(STUDENTS).also {
        it.studentId = UuidCreator.getTimeOrderedEpoch().toString()
        it.name = body.name
        it.identityId = body.identityId
        it.store()
    }

    fun update(studentId: String, body: StudentRequestBody) = jooq.update(STUDENTS)
        .set(STUDENTS.NAME, body.name)
        .set(STUDENTS.IDENTITY_ID, body.identityId)
        .where(STUDENTS.STUDENT_ID.eq(studentId))
        .execute()

    fun delete(studentId: String) = jooq.delete(STUDENTS)
        .where(STUDENTS.STUDENT_ID.eq(studentId))
        .execute()
}
