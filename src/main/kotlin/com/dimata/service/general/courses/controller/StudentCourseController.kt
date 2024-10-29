package com.dimata.service.general.courses.controller

import com.dimata.service.general.courses.model.request.StudentCourseRequestBody
import com.dimata.service.general.courses.service.StudentCourseService
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/api/student_courses")
class StudentCourseController(
    private val studentCourseService: StudentCourseService
) {
    @GET
    fun getAllStudentCourses(): Response {
        return Response.ok(studentCourseService.getAll()).build()
    }

    @POST
    fun createStudentCourse(studentCourse: StudentCourseRequestBody): Response {
        return Response.ok(studentCourseService.create(studentCourse)).build()
    }

    @DELETE
    @Path("/{studentId}/{courseId}")
    fun deleteStudentCourse(
        @PathParam("studentId") studentId: String,
        @PathParam("courseId") courseId: String
    ): Response {
        studentCourseService.delete(studentId, courseId)
        return Response.noContent().build()
    }
}
