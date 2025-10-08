package com.example.mythicalthaiapp.model


import android.content.Context
import com.example.mythicalthaiapp.model.CreatureFavorite
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesStorage {
    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorites_list"

    fun saveFavorites(context: Context, favorites: List<CreatureFavorite>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(favorites)
        prefs.edit().putString(KEY_FAVORITES, json).apply()
    }

    fun loadFavorites(context: Context): List<CreatureFavorite> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
        val type = object : TypeToken<List<CreatureFavorite>>() {}.type
        return Gson().fromJson(json, type)
    }
}
