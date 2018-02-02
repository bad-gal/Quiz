package com.developments.knowledgehut.quiz

data class AnsweredQuiz(
        val questions: List<String>,
        val answers: List<String>,
        val userAnswers: List <String>
)
