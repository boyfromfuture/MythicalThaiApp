package com.example.mythicalthaiapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mythicalthaiapp.databinding.ItemCreatureBinding
import com.example.mythicalthaiapp.model.Creature

class CreatureAdapter(
    private val onClick: (Creature) -> Unit
) : ListAdapter<Creature, CreatureAdapter.CreatureViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Creature>() {
        override fun areItemsTheSame(oldItem: Creature, newItem: Creature) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Creature, newItem: Creature) = oldItem == newItem
    }

    inner class CreatureViewHolder(private val binding: ItemCreatureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(creature: Creature) {
            binding.tvName.text = creature.name
            Glide.with(binding.root.context)
                .load(creature.image_url)
                .into(binding.ivCreature)

            binding.root.setOnClickListener { onClick(creature) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreatureViewHolder {
        val binding = ItemCreatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreatureViewHolder, position: Int) {
        val item = getItem(position)
        println("🔍 Binding creature at $position: ${item.name}") // debug log
        holder.bind(item)
    }

}

