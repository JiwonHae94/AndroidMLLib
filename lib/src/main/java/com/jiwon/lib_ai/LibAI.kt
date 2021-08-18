package com.jiwon.lib_ai

import android.content.Context
import java.lang.NullPointerException

class LibAI private constructor(private val context : Context){
    val applicationContext : Context
        get() { return context }

    companion object{
        private var libAI : LibAI ? = null
        private var context : Context? = null

        fun init(context:Context){
            libAI?.let{ return }

            synchronized(this){
                this.context = context
                libAI = LibAI(context)
            }
        }

        @Throws(NullPointerException::class)
        fun getApplicationContext() : Context{
            context?.let{} ?: throw NullPointerException("library has not been initialized")
            return context!!
        }

        @Throws(NullPointerException::class)
        fun getInstance() : LibAI{
            libAI?.let{} ?: throw NullPointerException("library has not been initialized")
            return libAI!!
        }

    }
}