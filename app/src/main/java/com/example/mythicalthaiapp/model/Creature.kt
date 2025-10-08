package com.example.mythicalthaiapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Creature(
    val id: Int,
    val name: String,
    val description: String,
    val image_url: String? = null
)
