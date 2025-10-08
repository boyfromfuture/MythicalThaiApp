package com.example.mythicalthaiapp.ui

import com.example.mythicalthaiapp.model.StoriesStorage
import com.example.mythicalthaiapp.model.FavoritesStorage
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mythicalthaiapp.databinding.ActivityCreatureDetailBinding
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.mythicalthaiapp.BuildConfig
import com.example.mythicalthaiapp.model.CreatureFavorite
import com.example.mythicalthaiapp.model.StoryFavorite
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class CreatureDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatureDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatureDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val supabase = SupabaseProvider.client

        // 🔹 Get creature info from Intent
        val creatureName = intent.getStringExtra("creature_name") ?: "Unknown Creature"
        val creatureDesc = intent.getStringExtra("creature_description") ?: "No description available."
        val creatureImage = intent.getStringExtra("creature_image") ?: ""

        // ✅ Bind UI
        binding.tvCreatureName.text = creatureName
        binding.tvCreatureInfo.text = creatureDesc

        Glide.with(this)
            .load(if (creatureImage.isNotEmpty()) creatureImage else "https://via.placeholder.com/300")
            .into(binding.imgCreature)

        // ❤️ Favorite creature button → save to "favorites" without duplicates
        binding.btnFavorite.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val exists = supabase.from("favorites").select {
                        filter { eq("name", creatureName) }
                        filter { eq("type", "creature") }
                    }.decodeList<CreatureFavorite>()

                    if (exists.isNotEmpty()) {
                        Toast.makeText(
                            this@CreatureDetailActivity,
                            "$creatureName is already in your favorites ❤️",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Insert into Supabase
                        supabase.from("favorites").insert(
                            mapOf(
                                "name" to creatureName,
                                "description" to creatureDesc,
                                "image_url" to creatureImage,
                                "type" to "creature"
                            )
                        )

                        // ✅ Also update local storage
                        val current = FavoritesStorage.loadFavorites(this@CreatureDetailActivity).toMutableList()
                        current.add(
                            CreatureFavorite(
                                id = java.util.UUID.randomUUID().toString(), // generate a local UUID
                                name = creatureName,
                                description = creatureDesc,
                                image_url = creatureImage,
                                type = "creature"
                            )
                        )
                        FavoritesStorage.saveFavorites(this@CreatureDetailActivity, current)

                        Toast.makeText(
                            this@CreatureDetailActivity,
                            "$creatureName added to favorites! ⭐",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@CreatureDetailActivity,
                        "❌ Failed to save creature: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


        // ✨ Generate AI Story button
        binding.btnGenerateStory.setOnClickListener {
            lifecycleScope.launch {
                try {
                    binding.cardStorySection.visibility = android.view.View.VISIBLE
                    binding.tvGeneratedStory.text = "✨ Generating story..."
                    val story = generateStory(creatureName, creatureDesc)
                    binding.tvGeneratedStory.text = story
                } catch (e: Exception) {
                    binding.tvGeneratedStory.text =
                        "❌ Failed to generate story: ${e.message}"
                }
            }
        }

        // ❤️ Favorite generated story button → save to "stories"
        binding.btnFavoriteStory.setOnClickListener {
            val storyText = binding.tvGeneratedStory.text.toString()
            if (storyText.isBlank() || storyText.startsWith("❌") || storyText.startsWith("✨")) {
                Toast.makeText(this, "No valid story to save!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val exists = supabase.from("stories").select {
                        filter { eq("creature_name", creatureName) }
                        filter { eq("story_text", storyText) }
                    }.decodeList<StoryFavorite>()

                    if (exists.isNotEmpty()) {
                        Toast.makeText(
                            this@CreatureDetailActivity,
                            "Story already saved ❤️",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // ✅ Insert into Supabase
                        supabase.from("stories").insert(
                            mapOf(
                                "creature_name" to creatureName,
                                "story_text" to storyText,
                                "image_url" to creatureImage
                            )
                        )

                        // ✅ Also update local storage
                        val current = StoriesStorage.loadStories(this@CreatureDetailActivity).toMutableList()
                        current.add(
                            StoryFavorite(
                                id = java.util.UUID.randomUUID().toString(), // local random id
                                creatureName = creatureName,
                                storyText = storyText,
                                imageUrl = creatureImage
                            )
                        )
                        StoriesStorage.saveStories(this@CreatureDetailActivity, current)

                        Toast.makeText(
                            this@CreatureDetailActivity,
                            "Story saved to favorites! ⭐",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@CreatureDetailActivity,
                        "❌ Failed to save story: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }



        // ⬅ Back button
        binding.btnBack.setOnClickListener { finish() }
    }

    // 🔥 Generate Gemini story
    private suspend fun generateStory(creatureName: String, creatureDesc: String): String {
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty()) throw Exception("⚠️ Gemini API Key is missing.")

            val model = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey
            )

            val prompt =
                "Write a playful short fictional story of about 5 sentences about the mythical creature $creatureName in Thai mythology. Here is information about the creature: $creatureDesc. Use this description to create an engaging story."

            val response = model.generateContent(content { text(prompt) })
            response.text ?: throw Exception("No story returned from Gemini")
        }
    }
}