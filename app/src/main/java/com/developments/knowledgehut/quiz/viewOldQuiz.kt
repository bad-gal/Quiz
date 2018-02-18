package com.developments.knowledgehut.quiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_old_quiz.*
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ViewOldQuiz : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menu = com.developments.knowledgehut.quiz.Menu()
        menu.menuSelection(this, item, this)
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_old_quiz)

        intent = this.intent
        if (intent.hasExtra("completed_quiz_row")) {
            val completedQuizRow  = intent.extras.get("completed_quiz_row")
            val dbHelper = DatabaseHandler(this, null)
            val quiz = dbHelper.findCompletedQuizByRow(completedQuizRow)
            val cat = quiz?.catId.toString()
            val category = dbHelper.findCategoryByCatId(cat)
            tv_category.text = category?.category
            tv_percent.text = "Correct: " + quiz?.percent?.let { Integer.toString(it) + "%" }
            val timestampToLong = quiz?.timestamp?.toLong()
            if (timestampToLong != null) {
                tv_date.text = "Completed: " + SimpleDateFormat("dd/MM/yyyy").format(Date(timestampToLong))
            }
            val completedList = quizDetails(quiz)
            val adapter = QuizAdapter(this, completedList)
            lv_old_quiz.adapter = adapter
        }
    }

    private fun quizDetails(quiz: CompletedQuiz?) :MutableList<OldResult> {
        val list = mutableListOf<OldResult>()
        quiz.let {
            val jsonObject = JSONObject(it?.results)
            val questionArray = jsonObject.getJSONArray("questions")
            val answerArray = jsonObject.getJSONArray("answers")
            val userAnswerArray = jsonObject.getJSONArray("userAnswers")

            for (i in 0 until questionArray.length()) {
                val question = "Q: " + StringEscapeUtils.unescapeHtml4(questionArray.get(i) as String)
                val answer = answerArray.get(i) as String
                var userAnswer = userAnswerArray.get(i) as String
                val correct = answer == userAnswer
                userAnswer = "A: " + userAnswer
                val result = OldResult(question, userAnswer, correct)
                list.add(result)
            }
        }
        return list
    }
}
