package com.example.mythicalthaiapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mythicalthaiapp.R
import com.example.mythicalthaiapp.model.CreatureFavorite

class FavoritesAdapter(
    private val onItemClick: (CreatureFavorite) -> Unit,
    private val onDeleteClick: (CreatureFavorite) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    private val items: MutableList<CreatureFavorite> = mutableListOf()

    fun submitList(list: List<CreatureFavorite>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class FavViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tvCreatureName) // ✅ fixed ID
        private val image: ImageView = view.findViewById(R.id.imgCreature)  // ✅ fixed ID
        private val deleteBtn: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(creature: CreatureFavorite) {
            name.text = creature.name

            Glide.with(itemView.context)
                .load(creature.image_url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(image)

            // open detail
            itemView.setOnClickListener {
                onItemClick(creature)
            }

            // delete
            deleteBtn.setOnClickListener {
                onDeleteClick(creature)

                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    items.removeAt(pos)
                    notifyItemRemoved(pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_creature_favorite, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
