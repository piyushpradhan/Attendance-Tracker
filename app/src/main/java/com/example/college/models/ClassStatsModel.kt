package com.example.college.models

class ClassStatsModel {
    var subject: String? = null
    var weekNo : Int? = null
    var weekList : ArrayList<Int>? = null

    constructor()

    constructor(subjectName : String?, currentWeek : Int?, emptyList: ArrayList<Int>?) {
        this.subject = subjectName
        this.weekNo = currentWeek
        this.weekList = emptyList
    }
}