package com.example.babysitbook.model

class TimeAsString (private val hour: Int, private val minute: Int){
    fun getTimeString(): String {
        var timeString = ""
        timeString += addZero(hour)
        timeString += hour
        timeString += ":"
        timeString += addZero(minute)
        timeString += minute
        return timeString
    }

    private fun addZero(timePart: Int) : String{
        if (timePart < 10){
            return "0"
        }
        return ""
    }
}