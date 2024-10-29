package com.dimata.service.general.courses.controller

import com.dimata.service.general.courses.model.request.ClassroomsRequestBody
import com.dimata.service.general.courses.repository.ClassroomsRepository
import com.dimata.service.general.courses.service.ClassroomsService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response

@Path("/api/classrooms")
class ClassroomsController (
    private val classroomsService: ClassroomsService,
    private val classroomsRepository: ClassroomsRepository
) {
    @GET
    fun getData(): Response {
        return Response.ok(classroomsRepository.getAll()).build()
    }

    @POST
    fun createClassroom(classroom: ClassroomsRequestBody): Response {
        return Response.ok(classroomsService.create(classroom)).build()
    }

    @PUT
    @Path("/{classroomId}")
    fun updateClassroom(@PathParam("classroomId") classroomId: String, classroom: ClassroomsRequestBody): Response {
        return Response.ok(classroomsService.update(classroomId, classroom)).build()
    }

    @DELETE
    @Path("/{classroomId}")
    fun deleteClassroom(@PathParam("classroomId") classroomId: String): Response {
        return Response.ok(classroomsService.delete(classroomId)).build()
    }
}