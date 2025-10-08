package com.example.mythicalthaiapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mythicalthaiapp.databinding.FragmentFavoritesListBinding
import com.example.mythicalthaiapp.model.CreatureFavorite
import com.example.mythicalthaiapp.model.FavoritesStorage
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteCreaturesFragment : Fragment() {

    private var _binding: FragmentFavoritesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesListBinding.inflate(inflater, container, false)

        // ✅ Adapter with callbacks
        favoritesAdapter = FavoritesAdapter(
            onItemClick = { creature ->
                val intent = Intent(requireContext(), CreatureDetailActivity::class.java).apply {
                    putExtra("creature_name", creature.name)
                    putExtra("creature_description", creature.description)
                    putExtra("creature_image", creature.image_url)
                }
                startActivity(intent)
            },
            onDeleteClick = { creature ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val client = SupabaseProvider.client
                        Log.d("Favorites", "Deleting creature with id=${creature.id}")

                        client.from("favorites").delete {
                            filter { eq("id", creature.id.toString()) }
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "${creature.name} removed from favorites ❌",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Update local cache immediately
                            val current = FavoritesStorage.loadFavorites(requireContext()).toMutableList()
                            current.removeAll { it.id == creature.id }
                            FavoritesStorage.saveFavorites(requireContext(), current)

                            favoritesAdapter.submitList(current)
                            toggleEmptyState(current.isEmpty())
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("Favorites", "Failed to delete", e)
                            Toast.makeText(
                                requireContext(),
                                "Failed to delete: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        )

        binding.recyclerFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesAdapter
        }

        // 🔹 First load from local cache
        val localFavorites = FavoritesStorage.loadFavorites(requireContext())
        if (localFavorites.isNotEmpty()) {
            favoritesAdapter.submitList(localFavorites)
            toggleEmptyState(false)
        }

        // 🔹 Then refresh from Supabase
        loadFavoritesFromSupabase()

        return binding.root
    }

    private fun loadFavoritesFromSupabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = SupabaseProvider.client
                val result = client.from("favorites").select {
                    filter { eq("type", "creature") }
                }
                val favorites = result.decodeList<CreatureFavorite>()

                Log.d("Favorites", "Loaded from Supabase: $favorites")

                // Save locally
                FavoritesStorage.saveFavorites(requireContext(), favorites)

                withContext(Dispatchers.Main) {
                    favoritesAdapter.submitList(favorites)
                    toggleEmptyState(favorites.isEmpty())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Favorites", "Failed to load Supabase", e)
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerFavorites.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerFavorites.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
