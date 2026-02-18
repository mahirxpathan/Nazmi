package com.mahiri.nazmi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahiri.nazmi.data.PoetryRepository
import com.mahiri.nazmi.model.Poem
import com.mahiri.nazmi.ui.add.AddPoemActivity
import com.mahiri.nazmi.ui.categories.CategoriesActivity
import com.mahiri.nazmi.ui.favorites.FavoritesActivity
import com.mahiri.nazmi.ui.home.HomeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var poemText: TextView
    private lateinit var heartButton: ImageButton
    private var currentFavorite = false
    private var currentPoemId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        poemText = findViewById(R.id.poemText)
        val refreshButton = findViewById<ImageButton>(R.id.refreshButton)
        heartButton = findViewById(R.id.heartButton)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        viewModel.getRandomPoemLive().observe(this) { poem ->
            if (poem != null) {
                renderPoem(poem)
            } else {
                poemText.setText(R.string.no_poems_yet)
            }
        }

        refreshButton.setOnClickListener {
            fadeView(poemText)
            viewModel.loadRandomPoem()
        }

        // Set Home as selected
        bottomNavigation.selectedItemId = R.id.nav_home
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home, refresh poem
                    viewModel.loadRandomPoem()
                    true
                }
                R.id.nav_categories -> {
                    val intent = Intent(this, CategoriesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_favorites -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_add_poem -> {
                    val intent = Intent(this, AddPoemActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        heartButton.setOnClickListener {
            if (currentPoemId > 0) {
                currentFavorite = !currentFavorite
                PoetryRepository(this).updateFavoriteStatus(currentPoemId, currentFavorite)
                updateHeartIcon()
            }
        }

        viewModel.loadRandomPoem()
    }

    private fun renderPoem(poem: Poem) {
        poemText.text = poem.content
        currentPoemId = poem.id
        currentFavorite = poem.isFavorite
        updateHeartIcon()
    }

    private fun fadeView(view: View) {
        val fade = AlphaAnimation(0f, 1f).apply {
            duration = 250
        }
        view.startAnimation(fade)
    }

    private fun updateHeartIcon() {
        if (::heartButton.isInitialized) {
            if (currentFavorite) {
                heartButton.setImageResource(R.drawable.ic_heart_filled)
                // Clear tint for filled heart - it uses its own colorError from drawable
                ImageViewCompat.setImageTintList(heartButton, null)
            } else {
                heartButton.setImageResource(R.drawable.ic_heart_outline)
                // The tint is already set in XML, but we need to restore it
                // The XML tint will be reapplied when the resource changes
                heartButton.clearColorFilter()
            }
        }
    }
}