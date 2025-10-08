package com.example.mythicalthaiapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mythicalthaiapp.databinding.FragmentFavoritesListBinding
import com.example.mythicalthaiapp.model.StoryFavorite
import com.example.mythicalthaiapp.model.StoriesStorage
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteStoriesFragment : Fragment() {

    private var _binding: FragmentFavoritesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var storiesAdapter: StoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesListBinding.inflate(inflater, container, false)

        // ✅ Adapter with delete callback
        storiesAdapter = StoriesAdapter(
            onItemClick = { story ->
                Toast.makeText(
                    requireContext(),
                    "Clicked story of ${story.creatureName}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDeleteClick = { story ->
                deleteStory(story)
            }
        )

        binding.recyclerFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storiesAdapter
        }

        // 🔹 Load local first
        val localStories = StoriesStorage.loadStories(requireContext())
        if (localStories.isNotEmpty()) {
            storiesAdapter.submitList(localStories)
            toggleEmptyState(false)
        } else {
            toggleEmptyState(true)
        }

        // 🔹 Then refresh from Supabase
        loadStoriesFromSupabase()

        return binding.root
    }

    // 🗑 Delete story from Supabase + local
    private fun deleteStory(story: StoryFavorite) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = SupabaseProvider.client
                Log.d("Stories", "Deleting story with id=${story.id}")

                // 🔥 Remove from Supabase
                client.from("stories").delete {
                    filter { eq("id", story.id) }
                }

                // 🔹 Remove from local storage
                val current = StoriesStorage.loadStories(requireContext()).toMutableList()
                current.removeAll { it.id == story.id }
                StoriesStorage.saveStories(requireContext(), current)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Story deleted ❌",
                        Toast.LENGTH_SHORT
                    ).show()
                    storiesAdapter.submitList(current)
                    toggleEmptyState(current.isEmpty())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 🔄 Fetch stories from Supabase
    private fun loadStoriesFromSupabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = SupabaseProvider.client
                val result = client.from("stories").select()
                val stories = result.decodeList<StoryFavorite>()

                // Save latest list locally
                StoriesStorage.saveStories(requireContext(), stories)

                withContext(Dispatchers.Main) {
                    storiesAdapter.submitList(stories)
                    toggleEmptyState(stories.isEmpty())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        binding.recyclerFavorites.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
