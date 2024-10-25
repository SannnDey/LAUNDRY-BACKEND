package com.dimata.service.general.courses.controller

import com.dimata.service.general.courses.model.request.TeacherRequestBody
import com.dimata.service.general.courses.repository.TeacherRepository
import com.dimata.service.general.courses.service.TeacherService
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/api/teachers")
class TeacherController(
    private val teacherService: TeacherService,
    private val teacherRepository: TeacherRepository
) {
    @GET
    fun getData(): Response {
        return Response.ok(teacherRepository.getAll()).build()
    }

    @POST
    fun createTeacher(teacher: TeacherRequestBody): Response {
        return Response.ok(teacherService.create(teacher)).build()
    }

    @PUT
    @Path("/{teacherId}")
    fun updateTeacher(@PathParam("teacherId") teacherId: String, teacher: TeacherRequestBody): Response {
        return Response.ok(teacherService.update(teacherId, teacher)).build()
    }

    @DELETE
    @Path("/{teacherId}")
    fun deleteTeacher(@PathParam("teacherId") teacherId: String): Response {
        teacherService.delete(teacherId)
        return Response.noContent().build()
    }
}
