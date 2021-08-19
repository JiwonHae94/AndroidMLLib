package com.jiwon.lib_ai.model.loader

import android.content.Context
import com.jiwon.lib_ai.model.RuntimeConfig
import java.io.IOException

abstract class ModelLoader<InterpreterT>(val context:Context)  {
    @Throws(IOException::class)
    abstract fun loadModel(path : String, modelName : String, loadOption : RuntimeConfig?) : InterpreterT

    @Throws(IOException::class)
    abstract fun loadAssetModel(modelName : String, loadOption: RuntimeConfig?) : InterpreterT
}