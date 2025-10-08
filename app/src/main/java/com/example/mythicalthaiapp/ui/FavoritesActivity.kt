package com.example.mythicalthaiapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mythicalthaiapp.databinding.ActivityFavoritesBinding
import com.google.android.material.tabs.TabLayoutMediator

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔙 Back to MainActivity
        binding.btnBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // ViewPager setup with adapter
        val pagerAdapter = FavoritesPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Tab titles
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Creatures"
                1 -> "Stories"
                else -> "Tab"
            }
        }.attach()
    }
}
