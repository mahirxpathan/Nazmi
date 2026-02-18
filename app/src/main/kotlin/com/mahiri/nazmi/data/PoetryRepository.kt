package com.mahiri.nazmi.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class PoetryRepository(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context.applicationContext)

    fun insertPoem(content: String, categoryId: Long, isUserAdded: Boolean): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_POETRY_CONTENT, content)
            put(DatabaseHelper.COL_POETRY_CATEGORY_ID, categoryId)
            put(DatabaseHelper.COL_POETRY_IS_FAVORITE, if (isUserAdded) 1 else 0)
            put(DatabaseHelper.COL_POETRY_IS_USER_ADDED, if (isUserAdded) 1 else 0)
        }
        return db.insert(DatabaseHelper.TABLE_POETRY, null, values)
    }

    fun getRandomPoem(): Cursor {
        val db = dbHelper.readableDatabase
        return db.rawQuery(
            "SELECT p.${DatabaseHelper.COL_POETRY_ID}, p.${DatabaseHelper.COL_POETRY_CONTENT}, p.${DatabaseHelper.COL_POETRY_CATEGORY_ID}, p.${DatabaseHelper.COL_POETRY_IS_FAVORITE} " +
                    "FROM ${DatabaseHelper.TABLE_POETRY} p ORDER BY RANDOM() LIMIT 1", null
        )
    }

    fun getRandomPoemExcluding(excludeIds: LongArray?): Cursor {
        val db = dbHelper.readableDatabase
        if (excludeIds == null || excludeIds.isEmpty()) {
            return getRandomPoem()
        }
        val placeholders = excludeIds.joinToString(",") { "?" }
        val sql = "SELECT p.${DatabaseHelper.COL_POETRY_ID}, p.${DatabaseHelper.COL_POETRY_CONTENT}, p.${DatabaseHelper.COL_POETRY_CATEGORY_ID}, p.${DatabaseHelper.COL_POETRY_IS_FAVORITE} " +
                "FROM ${DatabaseHelper.TABLE_POETRY} p WHERE p.${DatabaseHelper.COL_POETRY_ID} NOT IN ($placeholders) ORDER BY RANDOM() LIMIT 1"
        val args = excludeIds.map { it.toString() }.toTypedArray()
        return db.rawQuery(sql, args)
    }

    fun getTotalPoemCount(): Int {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_POETRY}", null)
        try {
            if (c.moveToFirst()) {
                return c.getInt(0)
            }
        } finally {
            c.close()
        }
        return 0
    }

    fun getPoemsByCategory(categoryId: Long): Cursor {
        val db = dbHelper.readableDatabase
        // Check if this is the "User Added" category
        val catCursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COL_CATEGORY_NAME} FROM ${DatabaseHelper.TABLE_CATEGORY} WHERE ${DatabaseHelper.COL_CATEGORY_ID} = ?",
            arrayOf(categoryId.toString())
        )
        var isUserAddedCategory = false
        if (catCursor != null && catCursor.moveToFirst()) {
            val categoryName = catCursor.getString(0)
            isUserAddedCategory = "User Added" == categoryName
        }
        catCursor?.close()

        val sql: String
        if (isUserAddedCategory) {
            // For "User Added" category, show all poems where is_user_added = 1
            sql = "SELECT ${DatabaseHelper.COL_POETRY_ID}, ${DatabaseHelper.COL_POETRY_CONTENT}, ${DatabaseHelper.COL_POETRY_CATEGORY_ID}, ${DatabaseHelper.COL_POETRY_IS_FAVORITE}, ${DatabaseHelper.COL_POETRY_IS_USER_ADDED} " +
                    "FROM ${DatabaseHelper.TABLE_POETRY} WHERE ${DatabaseHelper.COL_POETRY_IS_USER_ADDED} = 1 ORDER BY ${DatabaseHelper.COL_POETRY_ID} DESC"
            return db.rawQuery(sql, null)
        } else {
            sql = "SELECT ${DatabaseHelper.COL_POETRY_ID}, ${DatabaseHelper.COL_POETRY_CONTENT}, ${DatabaseHelper.COL_POETRY_CATEGORY_ID}, ${DatabaseHelper.COL_POETRY_IS_FAVORITE}, ${DatabaseHelper.COL_POETRY_IS_USER_ADDED} " +
                    "FROM ${DatabaseHelper.TABLE_POETRY} WHERE ${DatabaseHelper.COL_POETRY_CATEGORY_ID} = ? ORDER BY ${DatabaseHelper.COL_POETRY_ID} DESC"
            return db.rawQuery(sql, arrayOf(categoryId.toString()))
        }
    }

    fun getFavorites(): Cursor {
        val db = dbHelper.readableDatabase
        val sql = "SELECT ${DatabaseHelper.COL_POETRY_ID} AS _id, " +
                "${DatabaseHelper.COL_POETRY_CONTENT}, " +
                "${DatabaseHelper.COL_POETRY_CATEGORY_ID}, " +
                "${DatabaseHelper.COL_POETRY_IS_FAVORITE} " +
                "FROM ${DatabaseHelper.TABLE_POETRY} " +
                "WHERE ${DatabaseHelper.COL_POETRY_IS_FAVORITE} = 1 " +
                "ORDER BY ${DatabaseHelper.COL_POETRY_ID} DESC"
        return db.rawQuery(sql, null)
    }

    fun updateFavoriteStatus(poemId: Long, isFavorite: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_POETRY_IS_FAVORITE, if (isFavorite) 1 else 0)
        }
        val rows = db.update(
            DatabaseHelper.TABLE_POETRY, values,
            "${DatabaseHelper.COL_POETRY_ID} = ?", arrayOf(poemId.toString())
        )
        return rows > 0
    }

    fun getCategories(): Cursor {
        val db = dbHelper.readableDatabase
        val sql = "SELECT ${DatabaseHelper.COL_CATEGORY_ID}, ${DatabaseHelper.COL_CATEGORY_NAME} FROM ${DatabaseHelper.TABLE_CATEGORY} ORDER BY ${DatabaseHelper.COL_CATEGORY_NAME} ASC"
        return db.rawQuery(sql, null)
    }

    fun getFirstCategoryIdOrInsertDefault(): Long {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(
            "SELECT ${DatabaseHelper.COL_CATEGORY_ID} FROM ${DatabaseHelper.TABLE_CATEGORY} ORDER BY ${DatabaseHelper.COL_CATEGORY_ID} LIMIT 1",
            null
        )
        try {
            if (c.moveToFirst()) {
                return c.getLong(0)
            }
        } finally {
            c.close()
        }
        // Should not happen due to seed, but ensure one exists
        val wdb = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_CATEGORY_NAME, "General")
        }
        return wdb.insert(DatabaseHelper.TABLE_CATEGORY, null, values)
    }

    fun getUserAddedCategoryId(): Long {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(
            "SELECT ${DatabaseHelper.COL_CATEGORY_ID} FROM ${DatabaseHelper.TABLE_CATEGORY} WHERE ${DatabaseHelper.COL_CATEGORY_NAME} = 'User Added' LIMIT 1",
            null
        )
        try {
            if (c.moveToFirst()) {
                return c.getLong(0)
            }
        } finally {
            c.close()
        }
        // Should not happen, but create if missing
        val wdb = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_CATEGORY_NAME, "User Added")
        }
        return wdb.insert(DatabaseHelper.TABLE_CATEGORY, null, values)
    }

    fun deletePoem(poemId: Long): Boolean {
        val db = dbHelper.writableDatabase
        // Only delete if it's a user-added poem
        val c = db.rawQuery(
            "SELECT ${DatabaseHelper.COL_POETRY_IS_USER_ADDED} FROM ${DatabaseHelper.TABLE_POETRY} WHERE ${DatabaseHelper.COL_POETRY_ID} = ?",
            arrayOf(poemId.toString())
        )
        try {
            if (c.moveToFirst()) {
                val isUserAdded = c.getInt(0) == 1
                if (isUserAdded) {
                    val rows = db.delete(
                        DatabaseHelper.TABLE_POETRY,
                        "${DatabaseHelper.COL_POETRY_ID} = ?", arrayOf(poemId.toString())
                    )
                    return rows > 0
                }
            }
        } finally {
            c.close()
        }
        return false
    }
}