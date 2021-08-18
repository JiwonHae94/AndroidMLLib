package com.jiwon.lib_ai.model.loader

import org.opencv.dnn.Model
import org.tensorflow.lite.Interpreter
import android.content.Context
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate

class TfiteLoader : ModelLoader<Interpreter> {
    private val loadOption : Interpreter.Options? = null
    private var gpuDelegate : GpuDelegate? = null

    constructor(context:Context) : super(context){
        val compatList = CompatibilityList()

        if (compatList.isDelegateSupportedOnThisDevice) {
            val delegateOptions = compatList.bestOptionsForThisDevice
            gpuDelegate = GpuDelegate(delegateOptions)
        }
    }

    private fun convertOption(loadOption : LoadOption?) : Interpreter.Options?{
        loadOption?.let{} ?: return null
        val interpreterOpt = Interpreter.Options()
        interpreterOpt.setNumThreads(loadOption.numThread)

        return interpreterOpt
    }


    override fun loadModel(modelName: String, loadOption: LoadOption?): Interpreter {
        val opt = loadOption ?:
    }

    override fun loadAssetModel(modelName: String): Interpreter {
        TODO("Not yet implemented")
    }
}