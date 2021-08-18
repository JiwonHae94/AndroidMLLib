package com.jiwon.lib_ai.model.loader

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

abstract class ModelLoader<InterpreterT>(val context:Context)  {

    @Throws(IOException::class)
    abstract fun loadModel(modelName : String, loadOption : LoadOption?) : InterpreterT

    @Throws(IOException::class)
    abstract fun loadAssetModel(modelName : String) : InterpreterT

    enum class Delegate{
        GPU, XNNPACK, CPU, VULKAN
    }

    class LoadOption private constructor(val builder : Builder){
        val numThread = builder.numThread
        val delegate = builder.delegate

        class Builder {
            var numThread = 4
            var delegate = Delegate.XNNPACK

            fun setNumThread(numThread : Int) : Builder{
                this.numThread = numThread
                return this
            }

            fun setDelegate(delegate : Delegate) : Builder{
                this.delegate = delegate
                return this
            }

            fun build() : LoadOption{
                return LoadOption(this)
            }
        }
    }

}