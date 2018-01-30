package com.developments.knowledgehut.quiz

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?,
                      version: Int): SQLiteOpenHelper(context, DATABASE_NAME, factory,
                        DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createCategoryTable = ("CREATE TABLE " +
                TABLE_CATEGORIES + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_CATEGORY_ID
                + " INTEGER," + COLUMN_CATEGORY + " TEXT )")
        db.execSQL(createCategoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "quizzes.db"
        private const val TABLE_CATEGORIES = "categories"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_CATEGORY_ID = "cat_id"
        private const val COLUMN_CATEGORY = "category"
    }

    fun addCategory(categories: Categories) {
        val values = ContentValues()
        values.put(COLUMN_CATEGORY_ID, categories.catId)
        values.put(COLUMN_CATEGORY, categories.category)

        val db = this.writableDatabase
        db.insert(TABLE_CATEGORIES, null, values)
        db.close()
    }

    fun findCategory(name: String): Categories? {
        val query = "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY = \"$name\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var category: Categories? = null

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(0))
            val catId = Integer.parseInt(cursor.getString(1))
            val catName = cursor.getString(2)
            category = Categories(id, catId, catName)
            cursor.close()
        }

        db.close()
        return category
    }
}