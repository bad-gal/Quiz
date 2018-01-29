package com.developments.knowledgehut.quiz

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var userAnswers = mutableListOf<String>()
        val buttonlist = mutableListOf<RadioButton>()

        intent = this.intent

        val text = getIntentData(intent, "category")
        val level = getIntentData(intent, "difficulty")
        val questions = getIntentData(intent, "questions") as List<String>
        val answers = getIntentData(intent, "answers") as List<String>
        val choices = getIntentData(intent, "choices") as Array<List<String>>

        tv_header.text = "$text - Difficulty: $level"
        tv_question.movementMethod = ScrollingMovementMethod()
        tv_question.text = questions[0]

        setRadioButtons(buttonlist, choices[0])

        rg_choices.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            userAnswers.add(radioButton.tag.toString())

            tv_question.text = questions[1] //display next question
            rg_choices.removeAllViews()
            setRadioButtons(buttonlist, choices[1])
        }
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
