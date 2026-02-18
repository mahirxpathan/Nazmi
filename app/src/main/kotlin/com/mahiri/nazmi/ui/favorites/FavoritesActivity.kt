package com.mahiri.nazmi.ui.favorites

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahiri.nazmi.R
import com.mahiri.nazmi.data.DatabaseHelper
import com.mahiri.nazmi.data.PoetryRepository
import com.mahiri.nazmi.ui.add.AddPoemActivity
import com.mahiri.nazmi.ui.categories.CategoriesActivity
import com.mahiri.nazmi.ui.viewer.PoemViewerActivity
import com.mahiri.nazmi.MainActivity

class FavoritesActivity : AppCompatActivity() {
    private var adapter: FavoritesCursorAdapter? = null
    private lateinit var repo: PoetryRepository
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        listView = findViewById(R.id.favoritesList)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        repo = PoetryRepository(this)

        refreshFavorites()

        listView.onItemClickListener = android.widget.AdapterView.OnItemClickListener { _, _, position, _ ->
            val cursor = adapter?.getItem(position) as Cursor
            val poemId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_ID))
            val i = Intent(this, PoemViewerActivity::class.java).apply {
                putExtra("poemId", poemId)
            }
            startActivity(i)
        }

        listView.onItemLongClickListener = android.widget.AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val cursor = adapter?.getItem(position) as Cursor
            val poemId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_ID))
            val ok = repo.updateFavoriteStatus(poemId, false)
            if (ok) {
                Toast.makeText(this, R.string.removed_favorite, Toast.LENGTH_SHORT).show()
                refreshFavorites()
            }
            true
        }
        
        // Set Favorites as selected
        bottomNavigation.selectedItemId = R.id.nav_favorites
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_categories -> {
                    val intent = Intent(this, CategoriesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_favorites -> {
                    // Already on favorites, do nothing
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
    }

    fun refreshFavorites() {
        val c = repo.getFavorites()
        if (adapter == null) {
            adapter = FavoritesCursorAdapter(this, c, 0, this)
            listView.adapter = adapter
        } else {
            adapter?.changeCursor(c)
        }
    }
}