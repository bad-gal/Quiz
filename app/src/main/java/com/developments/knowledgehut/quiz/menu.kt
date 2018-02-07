package com.developments.knowledgehut.quiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast

class Menu {
    internal fun menuSelection(context: Context, item: MenuItem, app: Activity) {

        when (item.itemId) {

            R.id.about ->
                Toast.makeText(context, "Trivia Quiz - KnowledgeHut Developments", Toast.LENGTH_LONG).show()
            R.id.exit ->
                app.finishAffinity()
            R.id.history -> {
                val historyIntent = Intent()
                historyIntent.setClass(context, SavedQuizzesActivity().javaClass)
                context.startActivity(historyIntent)
            }
        }
    }
}