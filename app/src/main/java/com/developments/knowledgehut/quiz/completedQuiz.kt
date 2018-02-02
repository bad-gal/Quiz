package com.developments.knowledgehut.quiz

data class CompletedQuiz(
        val id: Int=0,
        var catId: Int=0,
        var difficulty: String ="",
        var results: String="",
        var percent: Int=0,
        var timestamp: String=""
) {
    constructor(catId: Int, difficulty: String, results: String, percent: Int, timestamp: String) : this() {
        this.catId = catId
        this.difficulty = difficulty
        this.results = results
        this.percent = percent
        this.timestamp = timestamp
    }

}