package com.example.todoapp.utils

import android.text.Editable
import java.util.UUID

/*

Transform from String to Editable method

 */
class StringUtils {

    companion object{
        private fun String.toEditable():Editable =  Editable.Factory.getInstance().newEditable(this)
        @JvmStatic
        fun Editable(s:String):Editable{
            return s.toEditable()
        }

    }
}