package com.mahiri.nazmi.ui.viewer

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahiri.nazmi.R
import com.mahiri.nazmi.data.DatabaseHelper
import com.mahiri.nazmi.data.PoetryRepository
import com.mahiri.nazmi.ui.add.AddPoemActivity
import com.mahiri.nazmi.ui.categories.CategoriesActivity
import com.mahiri.nazmi.ui.favorites.FavoritesActivity
import com.mahiri.nazmi.MainActivity

class PoemViewerActivity : AppCompatActivity() {

    private var poemId: Long = 0
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poem_viewer)

        poemId = intent.getLongExtra("poemId", -1)
        val poemText = findViewById<TextView>(R.id.viewerPoemText)
        val heartButton = findViewById<ImageButton>(R.id.heartButton)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val repo = PoetryRepository(this)

        // Load poem by id using a Cursor query
        val sql = "SELECT ${DatabaseHelper.COL_POETRY_CONTENT}, ${DatabaseHelper.COL_POETRY_IS_FAVORITE} FROM ${DatabaseHelper.TABLE_POETRY} WHERE ${DatabaseHelper.COL_POETRY_ID} = ? LIMIT 1"
        val c: Cursor = DatabaseHelper(this).readableDatabase.rawQuery(sql, arrayOf(poemId.toString()))
        try {
            if (c.moveToFirst()) {
                val content = c.getString(0)
                isFavorite = c.getInt(1) == 1
                poemText.text = content
            }
        } finally {
            c.close()
        }

        updateHeartIcon(heartButton)

        heartButton.setOnClickListener {
            isFavorite = !isFavorite
            repo.updateFavoriteStatus(poemId, isFavorite)
            updateHeartIcon(heartButton)
        }
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, CategoriesActivity::class.java))
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.nav_add_poem -> {
                    startActivity(Intent(this, AddPoemActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun updateHeartIcon(heartButton: ImageButton) {
        val icon = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        heartButton.setImageResource(icon)
        heartButton.setColorFilter(ContextCompat.getColor(this, if (isFavorite) R.color.nazmi_fav_light else R.color.nazmi_icon_light))
    }
}