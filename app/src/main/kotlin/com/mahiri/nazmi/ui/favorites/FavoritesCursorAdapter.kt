package com.mahiri.nazmi.ui.favorites

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.mahiri.nazmi.R
import com.mahiri.nazmi.data.DatabaseHelper
import com.mahiri.nazmi.data.PoetryRepository

class FavoritesCursorAdapter(
    context: Context,
    c: Cursor?,
    flags: Int,
    private val activity: FavoritesActivity
) : CursorAdapter(context, c, flags) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val repository: PoetryRepository = PoetryRepository(context)

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return inflater.inflate(R.layout.item_poem, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val text = view.findViewById<TextView>(R.id.textPoem)
        val heart = view.findViewById<ImageButton>(R.id.btnHeart)

        val id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_CONTENT))
        val isFav = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_IS_FAVORITE)) == 1

        text.text = content
        heart.setImageResource(if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)

        heart.setOnClickListener {
            val newFav = !isFav
            repository.updateFavoriteStatus(id, newFav)
            // Refresh the list
            activity.refreshFavorites()
        }
    }
}