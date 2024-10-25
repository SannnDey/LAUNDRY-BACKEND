package com.dimata.service.general.courses.repository

import com.dimata.service.general.courses.core.orm.JooqRepository
import com.dimata.service.general.courses.jooq.gen.Tables.COURSES_
import com.dimata.service.general.courses.jooq.gen.Tables.TEACHERS
import com.dimata.service.general.courses.model.request.CourseRequestBody
import com.dimata.service.general.courses.model.response.CourseResponse
import com.github.f4b6a3.uuid.UuidCreator
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CourseRepository : JooqRepository() {
    fun getAll(): List<CourseResponse> {
        return jooq
            .select(
                COURSES_.COURSE_ID,
                COURSES_.NAME,
                TEACHERS.NAME
            )
            .from(COURSES_)
            .join(TEACHERS).on(COURSES_.TEACHER_ID.eq(TEACHERS.TEACHER_ID))
            .fetch { record ->
                CourseResponse(
                    courseId = record[COURSES_.COURSE_ID],
                    name = record[COURSES_.NAME],
                    teacherName = record[TEACHERS.NAME]
                )
            }
    }

    fun create(body: CourseRequestBody) {
        val courseId = UuidCreator.getTimeOrderedEpoch().toString()
        jooq.newRecord(COURSES_, body).also {
            it.courseId = courseId
            it.store()
        }
    }

    fun update(courseId: String, body: CourseRequestBody) {
        jooq.update(COURSES_)
            .set(COURSES_.NAME, body.name)
            .set(COURSES_.TEACHER_ID, body.teacherId)
            .where(COURSES_.COURSE_ID.eq(courseId))
            .execute()
    }

    fun delete(courseId: String) {
        jooq.delete(COURSES_)
            .where(COURSES_.COURSE_ID.eq(courseId))
            .execute()
    }
}