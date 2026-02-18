package com.mahiri.nazmi.ui.categories

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahiri.nazmi.R
import com.mahiri.nazmi.data.PoetryRepository
import com.mahiri.nazmi.ui.add.AddPoemActivity
import com.mahiri.nazmi.ui.favorites.FavoritesActivity
import com.mahiri.nazmi.MainActivity

class PoemsByCategoryActivity : AppCompatActivity() {

    private var adapter: PoemsCursorRecyclerAdapter? = null
    private lateinit var repository: PoetryRepository
    private var categoryId: Long = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poems_by_category)

        categoryId = intent.getLongExtra("categoryId", -1)
        val categoryName = intent.getStringExtra("categoryName")
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        tb?.title = categoryName

        recyclerView = findViewById(R.id.recyclerView)
        emptyView = findViewById(R.id.empty_view)
        val deleteHint = findViewById<TextView>(R.id.deleteHint)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        recyclerView.layoutManager = LinearLayoutManager(this)

        repository = PoetryRepository(this)
        val isUserAddedCategory = "User Added" == categoryName

        // Show delete hint only for "User Added" category
        deleteHint.visibility = if (isUserAddedCategory) View.VISIBLE else View.GONE

        refreshPoems()
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_categories -> {
                    finish() // Go back to categories
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

    fun refreshPoems() {
        val cursor: Cursor? = repository.getPoemsByCategory(categoryId)
        if (adapter == null) {
            adapter = PoemsCursorRecyclerAdapter(this, cursor, categoryId, this)
            recyclerView.adapter = adapter
        } else {
            adapter?.swapCursor(cursor)
        }

        if (cursor == null || cursor.count == 0) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}