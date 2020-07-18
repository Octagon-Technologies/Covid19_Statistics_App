package com.example.covid19moniterapp

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("setDate")
fun TextView.setDate(date: String){
    var stringArray = ""
    for(char in date){
        if(char.toString() != "T"){
            stringArray += "$char"
        }
        else{
            break
        }
    }

    val weekDayString = SimpleDateFormat("yyyy-MM-dd",
        Locale.getDefault()).parse(stringArray)
    val longDate = weekDayString?.time
//      2020-03-18

    val newString = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(longDate)

    text = newString
}

