package com.developments.knowledgehut.quiz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_title.text = getText(R.string.topics)

        val urlResult = getCategories("https://opentdb.com/api_category.php")
        val categoryList = convertJsonToList(urlResult)

        val adapter = ArrayAdapter<String>(this, R.layout.list_item_quiz, categoryList)
        lv_topics.adapter = adapter
    }

    private fun getCategories(urlString: String): String {
        val getRequest = HttpGetRequest()
        return getRequest.execute(urlString).get()
    }

    private fun convertJsonToList(jsonString: String): List<String> {
        val categoryList = mutableListOf<String>()
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("trivia_categories")

        (0 until jsonArray.length() - 1)
                .map { jsonArray.get(it) as JSONObject }
                .mapTo(categoryList) { it.get("name") as String }

        return categoryList
    }

}
