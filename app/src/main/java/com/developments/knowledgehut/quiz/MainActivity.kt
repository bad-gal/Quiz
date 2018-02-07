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
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @SuppressLint("ShowToast")
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
                    val noOfQuestions = getNumberOfQuestions(category, radioButtonText)

                    if (noOfQuestions > 0) {
                        val questionUrl = retrieveUserSelectedQuiz(category, radioButtonText, noOfQuestions)

                        if (questionUrl != "") {
                            val urlQuestions = getData(questionUrl)
                            val (questions, choices, answers) = convertJsonToQuestionList(urlQuestions)

                            val detailIntent = Intent()
                            detailIntent.setClass(this, DetailActivity().javaClass)
                            detailIntent.putExtra("category_id", category?.catId)
                            detailIntent.putExtra("category", adapt.adapter.getItem(i).toString())
                            detailIntent.putExtra("difficulty", radioButtonText)
                            detailIntent.putStringArrayListExtra("questions", questions as ArrayList<String>?)
                            detailIntent.putStringArrayListExtra("answers", answers as ArrayList<String>)

                            val bundle = Bundle()
                            bundle.putSerializable("choices", choices as Serializable)
                            detailIntent.putExtras(bundle)
                            startActivity(detailIntent)
                        }
                    } else {
                       val toast =  Toast.makeText(this, "No questions could be found!", Toast.LENGTH_LONG)
                        toast.show()
                    }
                })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menu = com.developments.knowledgehut.quiz.Menu()
        menu.menuSelection(this, item, this)
        return super.onOptionsItemSelected(item)
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

    private fun convertJsonToQuestionCount(jsonString: String, difficulty: String): Int {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONObject("category_question_count")

                    when (difficulty) {
                        "Easy" -> return jsonArray.get("total_easy_question_count") as Int
                        "Medium" -> return jsonArray.get("total_medium_question_count") as Int
                        "Hard" -> return jsonArray.get("total_hard_question_count") as Int
                    }

        return 0
    }

    private fun retrieveUserSelectedQuiz(categoryEntry: Categories?, level: String, amount: Int): String {
        val newAmount: Int = if (amount > 10) { 10 } else amount

        val baseUrl = "https://opentdb.com/api.php?amount=$newAmount&"

        val category: String
        val difficulty = "&difficulty=" + level.toLowerCase()

        if (categoryEntry != null) {
            category = "category=" + categoryEntry.catId
            println(baseUrl + category + difficulty)
            return baseUrl + category + difficulty
        }
        return ""
    }

    private fun getNumberOfQuestions(categoryEntry: Categories?, level: String): Int {
        if (categoryEntry != null) {
            val baseUrl = "https://opentdb.com/api_count.php?category=${categoryEntry.catId}"
            val questionStats = getData(baseUrl)
            return convertJsonToQuestionCount(questionStats, level)
        }
        return 0
    }

    private fun getCategoryItem(name: String): Categories? {
        val dbHelper = DatabaseHandler(this, null, null, 1)
        return dbHelper.findCategory(name)
    }
}
