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


        val createCompletedQuizTable = ("CREATE TABLE IF NOT EXISTS " +
                TABLE_COMPLETED_QUIZ + "(" +
                CQ_COLUMN_ID + " INTEGER PRIMARY KEY," +
                CQ_COLUMN_CATEGORY_ID + " INTEGER," +
                CQ_DIFFICULTY + " TEXT," +
                CQ_COLUMN_RESULTS + " TEXT," +
                CQ_PERCENTAGE + " INTEGER," +
                CQ_TIMESTAMP + " TEXT)")

        db.execSQL(createCategoryTable)
        db.execSQL(createCompletedQuizTable)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLETED_QUIZ)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "test_quiz.db"
        private const val TABLE_CATEGORIES = "categories"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_CATEGORY_ID = "cat_id"
        private const val COLUMN_CATEGORY = "category"
        private const val TABLE_COMPLETED_QUIZ = "completed_quiz"
        private const val CQ_COLUMN_ID = "_id"
        private const val CQ_COLUMN_CATEGORY_ID = "cat_id"
        private const val CQ_COLUMN_RESULTS = "results"
        private const val CQ_PERCENTAGE = "percentage"
        private const val CQ_TIMESTAMP = "timestamp"
        private const val CQ_DIFFICULTY = "difficulty"
    }

    fun recordExists(catId: String): Boolean {
        val query = "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY_ID=\"$catId\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun addCategory(categories: Categories) {
        if (!recordExists((categories.catId).toString())) {
            val values = ContentValues()
            values.put(COLUMN_CATEGORY_ID, categories.catId)
            values.put(COLUMN_CATEGORY, categories.category)

            val db = this.writableDatabase
            db.insert(TABLE_CATEGORIES, null, values)
            db.close()
        }
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

    fun findCategoryByCatId(catId: String): Categories? {
        val query = "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY_ID = \"$catId\""
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

    fun addCompletedQuiz(completedQuiz: CompletedQuiz) {
        val values = ContentValues()
        values.put(CQ_COLUMN_CATEGORY_ID, completedQuiz.catId)
        values.put(CQ_DIFFICULTY, completedQuiz.difficulty)
        values.put(CQ_COLUMN_RESULTS, completedQuiz.results)
        values.put(CQ_PERCENTAGE, completedQuiz.percent)
        values.put(CQ_TIMESTAMP, completedQuiz.timestamp)

        val db = this.writableDatabase
        db.insert(TABLE_COMPLETED_QUIZ, null, values)
        db.close()
    }

    fun findAllCompletedQuizzes(): List<CompletedQuiz?> {
        val list: MutableList<CompletedQuiz> = mutableListOf()
        val query = "SELECT * FROM $TABLE_COMPLETED_QUIZ"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var completed: CompletedQuiz?

        if (cursor.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(0))
                val catId = Integer.parseInt(cursor.getString(1))
                val difficulty = cursor.getString(2)
                val results = cursor.getString(3)
                val percentage = Integer.parseInt(cursor.getString(4))
                val date = cursor.getString(5)
                completed = CompletedQuiz(id, catId, difficulty, results, percentage, date)
                list.add(completed)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return list
    }
}