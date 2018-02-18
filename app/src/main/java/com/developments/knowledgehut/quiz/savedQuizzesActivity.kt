package com.developments.knowledgehut.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_completed_quizzes.*
import java.text.SimpleDateFormat
import java.util.*

class SavedQuizzesActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_quizzes)

        val completedList = getCompletedQuizList()
        val adapter = ArrayAdapter<String>(this, R.layout.list_item_quiz, completedList)
        lv_quiz_completed.adapter = adapter

        lv_quiz_completed.onItemClickListener= AdapterView.OnItemClickListener(
                { _: AdapterView<*>, _: View, i: Int, _: Long ->
                    val oldQuizIntent = Intent()
                    oldQuizIntent.setClass(this, ViewOldQuiz().javaClass)
                    oldQuizIntent.putExtra("completed_quiz_row", i + 1)
                    startActivity(oldQuizIntent)
                }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menu = com.developments.knowledgehut.quiz.Menu()
        menu.menuSelection(this, item, this)
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCompletedQuizList() : List<String?> {
        val dbHelper = DatabaseHandler(this, null)
        val list = dbHelper.findAllCompletedQuizzes()

        val convertedList = mutableListOf<String>()
        for (item in list) {
            val category = dbHelper.findCategoryByCatId("${item?.catId}")
            val correct = item?.percent
            val timeStampToLong = item?.timestamp?.toLong()
            if (timeStampToLong != null) {
                val date = Date(timeStampToLong)
                val mydate = SimpleDateFormat("dd/MM/yyyy").format(date)
                val string = "Category: ${category?.category} \nDate: $mydate \nCorrect: $correct%"
                convertedList.add(string)
            }
        }
        return convertedList
    }
}
