package com.developments.knowledgehut.quiz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_completed_quizzes.*

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menu = com.developments.knowledgehut.quiz.Menu()
        menu.menuSelection(this, item, this)
        return super.onOptionsItemSelected(item)
    }

    private fun getCompletedQuizList() : List<String> {
        val list = mutableListOf<String>()

        list.add("A")
        list.add("B")
        list.add("C")

        return list
    }
    //need to display category name
    // I already have the ID from the completed quiz table, I just need to search the category table for the text

    //need to display the difficulty
    //I already have this in the completed quiz table

    //need to display the date
    //I already have this in the completed quiz table, I need to convert it into a readable date
}