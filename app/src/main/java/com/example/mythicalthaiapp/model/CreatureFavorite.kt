package com.example.mythicalthaiapp.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatureFavorite(
    val id: String,                // UUID
    val name: String,
    val description: String,
    val image_url: String,
    val type: String? = null,      // optional
    val created_at: String? = null // optional timestamp
)
