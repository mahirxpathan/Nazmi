package com.mahiri.nazmi.ui.home

import android.app.Application
import android.content.SharedPreferences
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mahiri.nazmi.data.PoetryRepository
import com.mahiri.nazmi.model.Poem
import kotlinx.coroutines.launch
import java.util.HashSet

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PoetryRepository = PoetryRepository(application)
    private val randomPoem = MutableLiveData<Poem>()
    private val prefs: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "nazmi_poem_cycle"
        private const val KEY_SHOWN_IDS = "shown_poem_ids"
        private const val KEY_TOTAL_COUNT = "total_poem_count"
    }

    fun getRandomPoemLive(): LiveData<Poem> {
        return randomPoem
    }

    fun loadRandomPoem() {
        viewModelScope.launch {
            // Get set of shown poem IDs
            val shownIdsSet = prefs.getStringSet(KEY_SHOWN_IDS, HashSet()) ?: HashSet()
            val totalCount = repository.getTotalPoemCount()
            val savedTotalCount = prefs.getInt(KEY_TOTAL_COUNT, 0)

            // If total count changed (new poems added) or all poems shown, reset cycle
            val newShownIdsSet = if (totalCount != savedTotalCount || shownIdsSet.size >= totalCount) {
                prefs.edit().putStringSet(KEY_SHOWN_IDS, HashSet()).putInt(KEY_TOTAL_COUNT, totalCount).apply()
                HashSet<String>()
            } else {
                shownIdsSet
            }

            // Convert to long array for query
            val excludeIds = newShownIdsSet.mapNotNull { idStr ->
                try {
                    idStr.toLong()
                } catch (e: NumberFormatException) {
                    null
                }
            }.toLongArray()

            // Get random poem excluding already shown ones
            val c: Cursor? = repository.getRandomPoemExcluding(excludeIds)
            try {
                if (c != null && c.moveToFirst()) {
                    val p = Poem().apply {
                        id = c.getLong(0)
                        content = c.getString(1)
                        categoryId = c.getLong(2)
                        isFavorite = c.getInt(3) == 1
                    }

                    // Add this poem ID to shown set (create new set for SharedPreferences)
                    val updatedShownIds = HashSet(newShownIdsSet).apply {
                        add(p.id.toString())
                    }
                    prefs.edit().putStringSet(KEY_SHOWN_IDS, updatedShownIds).apply()

                    randomPoem.postValue(p)
                } else {
                    // No more unseen poems, reset and try again
                    prefs.edit().putStringSet(KEY_SHOWN_IDS, HashSet()).apply()
                    loadRandomPoem() // Recursive call to get a fresh poem
                }
            } finally {
                c?.close()
            }
        }
    }
}