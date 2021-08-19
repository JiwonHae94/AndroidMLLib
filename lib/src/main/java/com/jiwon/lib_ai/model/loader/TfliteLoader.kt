package com.jiwon.lib_ai.model.loader

import org.tensorflow.lite.Interpreter
import android.content.Context
import com.jiwon.lib_ai.model.RuntimeConfig
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import java.io.File
import java.nio.ByteBuffer

class TfliteLoader : ModelLoader<Interpreter> {
    private val loadOption : Interpreter.Options? = null
    private var gpuDelegate : GpuDelegate? = null

    constructor(context:Context) : super(context){
        val compatList = CompatibilityList()

        if (compatList.isDelegateSupportedOnThisDevice) {
            val delegateOptions = compatList.bestOptionsForThisDevice
            gpuDelegate = GpuDelegate(delegateOptions)
        }
    }

    private fun convertOption(loadOption : RuntimeConfig?) : Interpreter.Options?{
        loadOption?.let{} ?: return null
        val interpreterOpt = Interpreter.Options()
        interpreterOpt.setNumThreads(loadOption.numThread)

        when(loadOption.delegate){
            RuntimeConfig.Delegate.XNNPACK-> interpreterOpt.setUseXNNPACK(true)
            RuntimeConfig.Delegate.GPU    -> interpreterOpt.addDelegate(gpuDelegate!!)
            RuntimeConfig.Delegate.NNAPI  -> interpreterOpt.setUseNNAPI(true)
        }

        return interpreterOpt
    }

    private val defaultOption : Interpreter.Options
        get() {
            val modelOption = Interpreter.Options()
            modelOption.setNumThreads(4)
            modelOption.setUseXNNPACK(true)
            return modelOption
        }

    private fun getTfliteModelRuntimeConfig(loadOption: RuntimeConfig?) : Interpreter.Options {
        return convertOption(loadOption) ?: defaultOption
    }

    override fun loadModel(path : String, modelName: String, loadOption: RuntimeConfig?): Interpreter {
        return Interpreter(ByteBuffer.wrap(File(path, modelName).readBytes()), getTfliteModelRuntimeConfig(loadOption))
    }

    override fun loadAssetModel(modelName: String, loadOption: RuntimeConfig?): Interpreter {
        return Interpreter(FileUtil.loadMappedFile(context, modelName), getTfliteModelRuntimeConfig(loadOption))
    }
}