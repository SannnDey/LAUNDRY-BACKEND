package com.dimata.service.general.courses.controller

import com.dimata.service.general.courses.model.request.StudentRequestBody
import com.dimata.service.general.courses.repository.StudentRepository
import com.dimata.service.general.courses.service.StudentService
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/api/students")
class StudentController(
    private val studentService: StudentService,
    private val studentRepository: StudentRepository
) {
    @GET
    fun getData(): Response {
        return Response.ok(studentRepository.getAll()).build()
    }

    @POST
    fun createStudent(student: StudentRequestBody): Response {
        return Response.ok(studentService.create(student)).build()
    }

    @PUT
    @Path("/{studentId}")
    fun updateStudent(@PathParam("studentId") studentId: String, student: StudentRequestBody): Response {
        return Response.ok(studentService.update(studentId, student)).build()
    }

    @DELETE
    @Path("/{studentId}")
    fun deleteStudent(@PathParam("studentId") studentId: String): Response {
        studentService.delete(studentId)
        return Response.noContent().build()
    }
}
