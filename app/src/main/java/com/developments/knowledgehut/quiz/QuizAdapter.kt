package com.developments.knowledgehut.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView



class QuizAdapter(context: Context, private val items: List<OldResult>): BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView: View = inflater.inflate(R.layout.list_old_quiz_item, parent, false)
        val titleTextView = rowView.findViewById(R.id.tv_question) as TextView
        val subtitleTextView = rowView.findViewById(R.id.tv_answer) as TextView
        val thumbnail = rowView.findViewById(R.id.img_answer_thumbnail) as ImageView

        val result: OldResult = getItem(position) as OldResult
        titleTextView.text = result.question
        subtitleTextView.text = result.answer
        if (result.correct) {
            thumbnail.setImageResource(R.drawable.hands_up)
        }
        else thumbnail.setImageResource(R.drawable.hands_down)

        return rowView
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}
