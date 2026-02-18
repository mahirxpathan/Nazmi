package com.mahiri.nazmi.ui.categories

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahiri.nazmi.R
import com.mahiri.nazmi.ui.add.AddPoemActivity
import com.mahiri.nazmi.ui.favorites.FavoritesActivity
import com.mahiri.nazmi.ui.home.HomeViewModel
import com.mahiri.nazmi.MainActivity
import com.mahiri.nazmi.data.PoetryRepository

class CategoriesActivity : AppCompatActivity() {
    private val categoryIds = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val listView = findViewById<ListView>(R.id.categoriesList)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val repo = PoetryRepository(this)

        val names = mutableListOf<String>()
        val c: Cursor? = repo.getCategories()
        try {
            if (c != null && c.moveToFirst()) {
                do {
                    categoryIds.add(c.getLong(0))
                    names.add(c.getString(1))
                } while (c.moveToNext())
            }
        } finally {
            c?.close()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val categoryId = categoryIds[position]
            val i = Intent(this, PoemsByCategoryActivity::class.java).apply {
                putExtra("categoryId", categoryId)
                putExtra("categoryName", names[position])
            }
            startActivity(i)
        }
        
        // Set Categories as selected
        bottomNavigation.selectedItemId = R.id.nav_categories
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_categories -> {
                    // Already on categories, do nothing
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
    }
}