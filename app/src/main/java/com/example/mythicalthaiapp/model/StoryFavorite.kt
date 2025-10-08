package com.example.mythicalthaiapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryFavorite(
    val id: String,
    @SerialName("creature_name") val creatureName: String,
    @SerialName("story_text") val storyText: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
