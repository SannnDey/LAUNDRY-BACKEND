package com.dimata.service.general.courses.model.response

data class StudentCourseResponse(
    val studentId: String,
    val courseId: String,
    val studentName: String,
    val courseName: String
)
