package com.developments.knowledgehut.quiz

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var text = ""
        var level = ""
        intent = this.intent
        if (intent.hasExtra("category")) {
            text = intent.getStringExtra("category")
            val dbHelper = DatabaseHandler(this, null, null, 1)
        }

        if (intent.hasExtra("difficulty")) {
            level = intent.getStringExtra("difficulty")
        }

        tv_header.text = "You clicked on $text and wants difficulty: $level"
    }
}
