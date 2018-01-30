package com.developments.knowledgehut.quiz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_title.text = getText(R.string.topics)

        var radioButtonText = "Easy"
        val urlResult = getData("https://opentdb.com/api_category.php")
        val categoryList = convertJsonToList(urlResult)
        val adapter = ArrayAdapter<String>(this, R.layout.list_item_quiz, categoryList)

        lv_categories.adapter = adapter

        rg_difficulty.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            radioButtonText = radioButton.text.toString()
        }

        lv_categories.onItemClickListener = AdapterView.OnItemClickListener(
                { adapt: AdapterView<*>, _: View, i: Int, _: Long ->

                    val category = getCategoryItem(adapt.adapter.getItem(i).toString())
                    val questionUrl = retrieveUserSelectedQuiz(category, radioButtonText)

                    if (questionUrl != "") {
                        val urlQuestions = getData(questionUrl)
                        val (questions, choices, answers) = convertJsonToQuestionList(urlQuestions)

                        val detailIntent = Intent()
                        detailIntent.setClass(this, DetailActivity().javaClass)
                        detailIntent.putExtra("category", adapt.adapter.getItem(i).toString())
                        detailIntent.putExtra("difficulty", radioButtonText)
                        detailIntent.putStringArrayListExtra("questions", questions as ArrayList<String>?)
                        detailIntent.putStringArrayListExtra("answers", answers as ArrayList<String>)

                        val bundle = Bundle()
                        bundle.putSerializable("choices", choices as Serializable)
                        detailIntent.putExtras(bundle)
                        startActivity(detailIntent)


                    }
                })
    }

    private fun getData(urlString: String): String {
        val getRequest = HttpGetRequest()
        return getRequest.execute(urlString).get()
    }

    private fun convertJsonToQuestionList(jsonString: String): Triple<MutableList<String>, Array<List<String>>, MutableList<String>> {

        val questionList = mutableListOf<String>()
        val answerList = mutableListOf<String>()
        val choiceList = Array<List<String>>(10) { mutableListOf()}

        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("results")

        for (i in 0 until jsonArray.length()) {
            val jObject: JSONObject = jsonArray.get(i) as JSONObject
            val question = jObject.get("question") as String
            val questionConvert = StringEscapeUtils.unescapeHtml4(question)
            questionList.add(questionConvert)

            val shortAnswer = mutableListOf<String>()
            val correctAnswer = jObject.get("correct_answer") as String
            val correctConvert = StringEscapeUtils.unescapeHtml4(correctAnswer)
            answerList.add(correctConvert)
            shortAnswer.add(correctConvert)

            val incorrectArray = jObject.getJSONArray("incorrect_answers")
            (0 until incorrectArray.length()).mapTo(shortAnswer) {
                StringEscapeUtils.unescapeHtml4(incorrectArray.get(it).toString())
            }
            shortAnswer.shuffle()
            choiceList[i] = shortAnswer

        }
        return Triple(questionList,choiceList, answerList)
    }

    private fun convertJsonToList(jsonString: String): List<String> {
        val dbHelper = DatabaseHandler(this, null, null, 1)
        val categoryList = mutableListOf<String>()
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("trivia_categories")

        for (i in 0 until jsonArray.length()) {
            val jObject: JSONObject = jsonArray.get(i) as JSONObject
            val id = jObject.get("id") as Int
            val strObject = jObject.get("name") as String
            val strConvert = StringEscapeUtils.unescapeHtml4(strObject)
            val category = Categories(id, strConvert)
            dbHelper.addCategory(category)
            categoryList.add(strObject)
        }
        return categoryList
    }

    private fun retrieveUserSelectedQuiz(categoryEntry: Categories?, level: String): String {
        val baseUrl = "https://opentdb.com/api.php?amount=10&"
        val category: String
        val difficulty = "&difficulty=" + level.toLowerCase()

        if (categoryEntry != null) {
            category = "category=" + categoryEntry.catId
            return baseUrl + category + difficulty
        }
        return ""
    }

    private fun getCategoryItem(name: String): Categories? {
        val dbHelper = DatabaseHandler(this, null, null, 1)
        return dbHelper.findCategory(name)
    }
}
