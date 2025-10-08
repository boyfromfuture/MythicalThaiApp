package com.example.mythicalthaiapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mythicalthaiapp.R
import com.example.mythicalthaiapp.model.StoryFavorite

class StoriesAdapter(
    private val onItemClick: (StoryFavorite) -> Unit,
    private val onDeleteClick: (StoryFavorite) -> Unit // ✅ new callback
) : RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    private var items: List<StoryFavorite> = emptyList()

    fun submitList(list: List<StoryFavorite>) {
        items = list
        notifyDataSetChanged()
    }

    inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvStoryTitle)
        private val content: TextView = view.findViewById(R.id.tvStoryContent)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteStory) // ✅ delete button

        fun bind(story: StoryFavorite) {
            title.text = story.creatureName
            content.text = story.storyText

            itemView.setOnClickListener {
                onItemClick(story)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(story) // ✅ call delete when pressed
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story_favorite, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
