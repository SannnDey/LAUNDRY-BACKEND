package com.dimata.service.general.courses.repository

import com.dimata.service.general.courses.core.orm.JooqRepository
import com.dimata.service.general.courses.model.response.ClassroomsResponse
import com.dimata.service.general.courses.jooq.gen.Tables.CLASSROOMS
import com.dimata.service.general.courses.jooq.gen.Tables.COURSES_
import com.dimata.service.general.courses.model.request.ClassroomsRequestBody
import com.github.f4b6a3.uuid.UuidCreator
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ClassroomsRepository : JooqRepository() {
    fun getAll(): List<ClassroomsResponse> {
        return jooq
            .select(
                CLASSROOMS.CLASSROOM_ID,
                COURSES_.NAME,
                CLASSROOMS.LOCATION,
                CLASSROOMS.CAPACITY
            )
            .from(CLASSROOMS)
            .join(COURSES_).on(CLASSROOMS.COURSE_ID.eq(COURSES_.COURSE_ID))
            .fetch{ record ->
                ClassroomsResponse (
                    classroomId = record[CLASSROOMS.CLASSROOM_ID],
                    courseName = record[COURSES_.NAME],
                    location = record[CLASSROOMS.LOCATION],
                    capacity = record[CLASSROOMS.CAPACITY]
                )

            }
    }

    fun create(body: ClassroomsRequestBody) {
        val classroomId = UuidCreator.getTimeOrderedEpoch().toString()
        jooq.newRecord(CLASSROOMS, body).also {
            it.classroomId = classroomId
            it.store()
        }
    }

    fun update(classroomId: String, body: ClassroomsRequestBody) {
        jooq.update(CLASSROOMS)
            .set(CLASSROOMS.COURSE_ID, body.courseId)
            .set(CLASSROOMS.LOCATION, body.location)
            .set(CLASSROOMS.CAPACITY, body.capacity)
            .where(CLASSROOMS.CLASSROOM_ID.eq(classroomId))
            .execute()
    }

    fun delete(classroomId: String) {
        jooq.delete(CLASSROOMS)
            .where(CLASSROOMS.CLASSROOM_ID.eq(classroomId))
            .execute()
    }
}