package com.developments.knowledgehut.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.RadioButton
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*

//TODO: Include Gson Json converter - DONE
//TODO: Create object of elements to be included in Json e.g questions etc - DONE
//TODO: Create database table that will accept the json - DONE
//TODO: Save user quiz details (json) to database
//TODO: Write method that allows user to view their past quizzes
//TODO: Write method that allows user to replay their past quizzes

class DetailActivity: AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val userAnswers = mutableListOf<String>()
        val buttonlist = mutableListOf<RadioButton>()
        var questionIndex = 0
        var radioChecked = 0
        var correct = 0

        intent = this.intent

        val categoryId = getIntentData(intent,"category_id") as Int
        val text = getIntentData(intent, "category")
        val level = getIntentData(intent, "difficulty") as String
        val questions = getIntentData(intent, "questions") as List<String>
        val answers = getIntentData(intent, "answers") as List<String>
        val choices = getIntentData(intent, "choices") as Array<List<String>>

        tv_header.text = "$text - Difficulty: $level"
        tv_question.movementMethod = ScrollingMovementMethod()
        tv_question.text = questions[0]

        btn_next.text = resources.getString(R.string.btn_next)
        btn_next.visibility = View.INVISIBLE

        progressBar2.max = questions.size
        progressBar2.progress = 0

        println(questions.toString())
        setRadioButtons(buttonlist, choices[0])

        rg_choices.setOnCheckedChangeListener { group, checkedId ->
            radioChecked = checkedId
            if (questionIndex < questions.size) {
                btn_next.visibility = View.VISIBLE
            }
        }

        btn_next.setOnClickListener {
            val radioButton = rg_choices.findViewById<RadioButton>(radioChecked)

            userAnswers.add(radioButton.tag.toString())
            if (radioButton.tag.toString() == answers[questionIndex]) {
                correct++
            }

            questionIndex++
            progressBar2.progress = questionIndex

            if (questionIndex == questions.size) {
                val percentage = showResults(questions.size, correct.toFloat())
                val convertIt = convertToJson(questions, answers, userAnswers) as String

                    val dbHelper = DatabaseHandler(this, null, null, 1)
                    val completedQuiz = CompletedQuiz(categoryId, level, convertIt, percentage, Date().time.toString())
                    dbHelper.addCompletedQuiz(completedQuiz)
                    val allQuizzesCompleted = dbHelper.findAllCompletedQuizzes()
                    println(allQuizzesCompleted.toString())

            } else {
                showNextQuestion(questionIndex, questions, buttonlist, choices)
                btn_next.visibility = View.INVISIBLE
            }
        }
    }

    private fun showNextQuestion(questionIndex: Int, questions: List<String>,
                                 buttonlist: MutableList<RadioButton>, choices: Array<List<String>>) {

        if (questionIndex < questions.size) {
            tv_question.text = questions[questionIndex]
            rg_choices.clearCheck()
            rg_choices.removeAllViews()
            setRadioButtons(buttonlist, choices[questionIndex])

            if (questionIndex == questions.size - 1) {
                btn_next.text = resources.getString(R.string.btn_results)
            } else btn_next.text = resources.getString(R.string.btn_next)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showResults(size: Int, correct: Float): Int {
        val percentage = (correct / size) * 100

        tv_question.text = "You got ${percentage.toInt()}% correct"
        btn_next.visibility = View.INVISIBLE
        rg_choices.clearCheck()
        rg_choices.removeAllViews()

        return percentage.toInt()
    }

    private fun getIntentData(intent: Intent, name: String): Any {
        return if (intent.hasExtra(name)) {
            intent.extras.get(name)
        } else ""
    }

    private fun setRadioButtons(radioButtonArray: MutableList<RadioButton>, choice: List<String>) {
        for (i in 0 until choice.size) {
            val radioButton = RadioButton(this)
            radioButton.typeface = Typeface.create("casual", Typeface.NORMAL)
            radioButton.id = i
            radioButton.text = choice[i]
            radioButton.tag = choice[i]
            radioButtonArray.add(radioButton)
            rg_choices.addView(radioButton)
        }
    }

    private fun convertToJson(question: List<String>, answer: List<String>, userChoice: List<String>): String? {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val answeredQuestions = AnsweredQuiz(question, answer, userChoice)
        return gson.toJson(answeredQuestions)
    }
}
