package com.mahiri.nazmi.ui.categories

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mahiri.nazmi.R
import com.mahiri.nazmi.data.DatabaseHelper
import com.mahiri.nazmi.data.PoetryRepository
import com.mahiri.nazmi.ui.viewer.PoemViewerActivity

class PoemsCursorRecyclerAdapter(
    private val context: Context,
    private var cursor: Cursor?,
    private val categoryId: Long,
    private val activity: PoemsByCategoryActivity?
) : RecyclerView.Adapter<PoemsCursorRecyclerAdapter.ViewHolder>() {

    private val repository: PoetryRepository = PoetryRepository(context)

    fun swapCursor(newCursor: Cursor?) {
        cursor?.close()
        cursor = newCursor
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_poem, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor == null || !cursor!!.moveToPosition(position)) return
        val id = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_ID))
        val content = cursor!!.getString(cursor!!.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_CONTENT))
        val isFav = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_IS_FAVORITE)) == 1
        var isUserAdded = false
        try {
            isUserAdded = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(DatabaseHelper.COL_POETRY_IS_USER_ADDED)) == 1
        } catch (e: Exception) {
            // Column might not exist in old cursors
        }

        holder.textPoem.text = content
        holder.btnHeart.setImageResource(if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)

        holder.itemView.setOnClickListener {
            val i = Intent(context, PoemViewerActivity::class.java).apply {
                putExtra("poemId", id)
            }
            context.startActivity(i)
        }

        // Long press for deletion (only for user-added poems)
        holder.itemView.setOnLongClickListener { v ->
            if (isUserAdded) {
                showDeleteDialog(id)
                true
            } else {
                false
            }
        }

        holder.btnHeart.setOnClickListener {
            repository.updateFavoriteStatus(id, !isFav)
            val updated = repository.getPoemsByCategory(categoryId)
            swapCursor(updated)
        }
    }

    private fun showDeleteDialog(poemId: Long) {
        AlertDialog.Builder(context)
            .setTitle(R.string.delete_poem_title)
            .setMessage(R.string.delete_poem_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                if (repository.deletePoem(poemId)) {
                    Toast.makeText(context, R.string.poem_deleted, Toast.LENGTH_SHORT).show()
                    activity?.refreshPoems()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textPoem: TextView = itemView.findViewById(R.id.textPoem)
        val btnHeart: ImageButton = itemView.findViewById(R.id.btnHeart)
    }
}