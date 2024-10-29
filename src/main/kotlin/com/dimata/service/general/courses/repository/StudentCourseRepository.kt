package com.dimata.service.general.courses.repository

import com.dimata.service.general.courses.core.orm.JooqRepository
import com.dimata.service.general.courses.jooq.gen.Tables.STUDENT_COURSES
import com.dimata.service.general.courses.jooq.gen.Tables.STUDENTS
import com.dimata.service.general.courses.jooq.gen.Tables.COURSES_
import com.dimata.service.general.courses.model.request.StudentCourseRequestBody
import com.dimata.service.general.courses.model.response.StudentCourseResponse
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StudentCourseRepository : JooqRepository() {
    fun getAll(): List<StudentCourseResponse> {
        return jooq
            .select(
                STUDENT_COURSES.STUDENT_ID,
                STUDENT_COURSES.COURSE_ID,
                STUDENTS.NAME,
                COURSES_.NAME
            )
            .from(STUDENT_COURSES)
            .join(STUDENTS).on(STUDENT_COURSES.STUDENT_ID.eq(STUDENTS.STUDENT_ID))
            .join(COURSES_).on(STUDENT_COURSES.COURSE_ID.eq(COURSES_.COURSE_ID))
            .fetch { record ->
                StudentCourseResponse(
                    studentId = record[STUDENT_COURSES.STUDENT_ID],
                    courseId = record[STUDENT_COURSES.COURSE_ID],
                    studentName = record[STUDENTS.NAME],
                    courseName = record[COURSES_.NAME]
                )
            }
    }

    fun create(body: StudentCourseRequestBody) {
        jooq.newRecord(STUDENT_COURSES).also {
            it.studentId = body.studentId
            it.courseId = body.courseId
            it.store()
        }
    }



    fun delete(studentId: String, courseId: String) {
        jooq.delete(STUDENT_COURSES)
            .where(STUDENT_COURSES.STUDENT_ID.eq(studentId))
            .and(STUDENT_COURSES.COURSE_ID.eq(courseId))
            .execute()
    }
}
