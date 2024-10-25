package com.dimata.service.general.courses.repository

import com.dimata.service.general.courses.core.orm.JooqRepository
import com.dimata.service.general.courses.jooq.gen.Tables.TEACHERS
import com.dimata.service.general.courses.model.request.TeacherRequestBody
import com.dimata.service.general.courses.model.response.TeacherResponse
import com.github.f4b6a3.uuid.UuidCreator
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TeacherRepository : JooqRepository() {
    fun getAll(): List<TeacherResponse> = jooq.selectFrom(TEACHERS).fetchInto(TeacherResponse::class.java)

    fun create(body: TeacherRequestBody) = jooq.newRecord(TEACHERS).also {
        it.teacherId = UuidCreator.getTimeOrderedEpoch().toString()
        it.name = body.name
        it.identityId = body.identityId
        it.store()
    }

    fun update(teacherId: String, body: TeacherRequestBody) = jooq.update(TEACHERS)
        .set(TEACHERS.NAME, body.name)
        .set(TEACHERS.IDENTITY_ID, body.identityId)
        .where(TEACHERS.TEACHER_ID.eq(teacherId))
        .execute()

    fun delete(teacherId: String) = jooq.delete(TEACHERS)
        .where(TEACHERS.TEACHER_ID.eq(teacherId))
        .execute()
}
