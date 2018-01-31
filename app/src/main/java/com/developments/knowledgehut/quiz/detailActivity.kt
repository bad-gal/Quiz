package com.developments.knowledgehut.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val userAnswers = mutableListOf<String>()
        val buttonlist = mutableListOf<RadioButton>()
        var questionIndex = 0
        var radioChecked = 0
        var correct = 0

        intent = this.intent

        val text = getIntentData(intent, "category")
        val level = getIntentData(intent, "difficulty")
        val questions = getIntentData(intent, "questions") as List<String>
        val answers = getIntentData(intent, "answers") as List<String>
        val choices = getIntentData(intent, "choices") as Array<List<String>>

        tv_header.text = "$text - Difficulty: $level"
        tv_question.movementMethod = ScrollingMovementMethod()
        tv_question.text = questions[0]

        btn_next.text = resources.getString(R.string.btn_next)
        btn_next.visibility = View.INVISIBLE

        progressBar2.max = 10
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
                showResults(questions.size, correct.toFloat())
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
    private fun showResults(size: Int, correct: Float) {
        val percentage = (correct / size) * 100

        tv_question.text = "You got ${percentage.toInt()}% correct"
        btn_next.visibility = View.INVISIBLE
        rg_choices.clearCheck()
        rg_choices.removeAllViews()
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
}
