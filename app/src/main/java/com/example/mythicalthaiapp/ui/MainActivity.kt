package com.example.mythicalthaiapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mythicalthaiapp.databinding.ActivityMainBinding
import com.example.mythicalthaiapp.model.Creature
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CreatureAdapter
    private var allCreatures = listOf<Creature>() // Store all creatures for filtering

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup adapter
        adapter = CreatureAdapter { creature ->
            val intent = Intent(this, CreatureDetailActivity::class.java)
            intent.putExtra("creature_id", creature.id)
            intent.putExtra("creature_name", creature.name)
            intent.putExtra("creature_description", creature.description)
            intent.putExtra("creature_image", creature.image_url)
            startActivity(intent)
        }

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Setup Search functionality
        setupSearch()

        // Setup Favorites button
        binding.btnFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        // Fetch creatures from Supabase
        fetchCreatures()
    }

    private fun setupSearch() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCreatures(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchCreatures(query: String) {
        val filteredList = if (query.isEmpty()) {
            allCreatures
        } else {
            allCreatures.filter { creature ->
                creature.name.startsWith(query, ignoreCase = true)
            }
        }
        adapter.submitList(filteredList)
    }

    private fun fetchCreatures() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val creatures = SupabaseProvider.client
                    .postgrest["creatures"]
                    .select()
                    .decodeList<Creature>()

                withContext(Dispatchers.Main) {
                    if (creatures.isEmpty()) {
                        println("⚠️ No creatures found in Supabase")
                    } else {
                        println("✅ Creatures loaded: ${creatures.size}")
                        allCreatures = creatures // Store for search filtering
                        adapter.submitList(creatures)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    println("❌ Error fetching creatures: ${e.message}")
                }
            }
        }
    }
}