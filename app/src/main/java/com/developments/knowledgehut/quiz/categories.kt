package com.developments.knowledgehut.quiz

data class Categories(val id: Int=0, var catId: Int=0, var category: String="") {

    constructor(catId: Int, category: String) : this() {
        this.catId = catId
        this.category = category
    }
}
