package com.dimata.service.general.courses.controller

import com.dimata.service.general.courses.model.request.CourseRequestBody
import com.dimata.service.general.courses.repository.CourseRepository
import com.dimata.service.general.courses.service.CourseService
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response

@Path("/api/courses")
class CourseController(
    private val courseService: CourseService,
    private val courseRepository: CourseRepository
) {
    @GET
    fun getData(): Response {
        return Response.ok(courseRepository.getAll()).build()
    }

    @POST
    fun createCourse(course: CourseRequestBody): Response {
        return Response.ok(courseService.create(course)).build()
    }

    @PUT
    @Path("/{courseId}")
    fun updateCourse(@PathParam("courseId") courseId: String, course: CourseRequestBody): Response {
        return Response.ok(courseService.update(courseId, course)).build()
    }

    @DELETE
    @Path("/{courseId}")
    fun deleteCourse(@PathParam("courseId") courseId: String): Response {
        return Response.ok(courseService.delete(courseId)).build()
    }
}
