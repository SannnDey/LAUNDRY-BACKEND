package com.dimata.service.general.courses.model.response

data class ClassroomsResponse(
    val classroomId: String,
    val courseName: String,
    val location: String,
    val capacity: Int
)
