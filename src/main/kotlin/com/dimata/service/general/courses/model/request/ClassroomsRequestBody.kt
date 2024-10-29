package com.dimata.service.general.courses.model.request

data class ClassroomsRequestBody(
    val courseId: String,
    val location: String,
    val capacity: Int

)
