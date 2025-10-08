package com.example.mythicalthaiapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mythicalthaiapp.databinding.BottomSheetStoriesBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.github.jan.supabase.postgrest.from

class StoryBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetStoriesBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_STORY = "arg_story"
        fun newInstance(story: String) = StoryBottomSheet().apply {
            arguments = Bundle().apply { putString(ARG_STORY, story) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val story = arguments?.getString(ARG_STORY) ?: "(no story)"
        binding.tvStories.text = story

        binding.btnCloseSheet.setOnClickListener { dismiss() }

        // Save story button: inserts into favorites table as a story
        binding.btnSaveStoryFromSheet.setOnClickListener {
            // Using coroutine to insert via SupabaseProvider
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    SupabaseProvider.client.from("favorites").insert(
                        mapOf(
                            "name" to "Story: ${requireActivity().title ?: "Creature"}",
                            "description" to story,
                            "image_url" to "" ,
                            "type" to "story"
                        )
                    )
                    // return to UI thread to show confirmation
                    CoroutineScope(Dispatchers.Main).launch {
                        // small feedback
                        dismiss()
                    }
                } catch (_: Exception) {
                    // ignore for now; you can surface an error message if you want
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
