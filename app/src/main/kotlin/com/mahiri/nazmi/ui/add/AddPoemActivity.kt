package com.mahiri.nazmi.ui.add

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.mahiri.nazmi.R
import com.mahiri.nazmi.data.PoetryRepository
import com.mahiri.nazmi.ui.categories.CategoriesActivity
import com.mahiri.nazmi.ui.favorites.FavoritesActivity
import com.mahiri.nazmi.MainActivity

class AddPoemActivity : AppCompatActivity() {

    private val categoryIds = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_poem)

        val poemInput = findViewById<EditText>(R.id.poemInput)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val saveButton = findViewById<MaterialButton>(R.id.saveButton)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val repo = PoetryRepository(this)

        val names = mutableListOf<String>()
        val c: Cursor? = repo.getCategories()
        try {
            if (c != null && c.moveToFirst()) {
                do {
                    val id = c.getLong(0)
                    val name = c.getString(1)
                    categoryIds.add(id)
                    names.add(name)
                } while (c.moveToNext())
            }
        } finally {
            c?.close()
        }

        if (names.isEmpty()) {
            val id = repo.getFirstCategoryIdOrInsertDefault()
            categoryIds.add(id)
            names.add("General")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
        categorySpinner.adapter = adapter

        saveButton.setOnClickListener {
            val content = poemInput.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(this, R.string.enter_poem_text, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val pos = categorySpinner.selectedItemPosition
            val catId = categoryIds.getOrElse(pos.coerceAtLeast(0)) { categoryIds[0] }
            val id = repo.insertPoem(content, catId, true)
            if (id > 0) {
                Toast.makeText(this, R.string.poem_saved, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show()
            }
        }
        
        // Set Add Poem as selected
        bottomNavigation.selectedItemId = R.id.nav_add_poem
        
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
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    true
                }
                R.id.nav_add_poem -> {
                    // Already on add poem, do nothing
                    true
                }
                else -> false
            }
        }
    }
}