package com.example.todoapp.utils

import android.text.Editable

class StringUtils {

    companion object{
        private fun String.toEditable():Editable =  Editable.Factory.getInstance().newEditable(this)

        @JvmStatic
        fun Editable(s:String):Editable{
            return s.toEditable()
        }

        @JvmStatic
        fun cutString(s:String):String{
            val s1=s.replace("\n", System.getProperty("line.separator")!!.toString());
            return if(s1.substringBefore("\n").length<=20) {
                if(s1 == s1.substringBefore("\n")) s1.substringBefore("\n")
                else "${s1.substringBefore("\n")}..."
            } else "${s1.substring(0,21)}..."
        }
    }
}