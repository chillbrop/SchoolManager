package com.example.schoolmanager

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val email: String = "",
    @SerialName("full_name") val fullName: String = "",
    val role: String = "student",
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class Assignment(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    @SerialName("due_date") val dueDate: String = "",
    @SerialName("teacher_id") val teacherId: String = "",
    @SerialName("teacher_name") val teacherName: String = "",
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    @SerialName("author_id") val authorId: String = "",
    @SerialName("author_name") val authorName: String = "",
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class NewAssignment(
    val title: String,
    val description: String,
    val subject: String,
    @SerialName("due_date") val dueDate: String,
    @SerialName("teacher_id") val teacherId: String,
    @SerialName("teacher_name") val teacherName: String
)

@Serializable
data class NewAnnouncement(
    val title: String,
    val content: String,
    @SerialName("author_id") val authorId: String,
    @SerialName("author_name") val authorName: String
)

@Serializable
data class NewProfile(
    val id: String,
    val email: String,
    @SerialName("full_name") val fullName: String,
    val role: String = "student"
)
