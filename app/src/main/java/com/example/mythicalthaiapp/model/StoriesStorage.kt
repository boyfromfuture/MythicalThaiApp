package com.example.mythicalthaiapp.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object StoriesStorage {
    private const val PREFS_NAME = "stories_prefs"
    private const val KEY_STORIES = "stories_list"

    fun saveStories(context: Context, stories: List<StoryFavorite>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(stories)
        prefs.edit().putString(KEY_STORIES, json).apply()
    }

    fun loadStories(context: Context): List<StoryFavorite> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_STORIES, null) ?: return emptyList()
        val type = object : TypeToken<List<StoryFavorite>>() {}.type
        return Gson().fromJson(json, type)
    }
}
