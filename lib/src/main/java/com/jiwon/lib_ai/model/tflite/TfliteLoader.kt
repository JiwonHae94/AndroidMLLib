package com.jiwon.lib_ai.model.tflite

import com.jiwon.lib_ai.LibAI
import com.jiwon.lib_ai.model.exe.ExecutionOption
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.FileUtil
import java.io.File
import java.lang.RuntimeException
import java.nio.ByteBuffer
import android.content.Context

class TfliteLoader: ModelLoader<Interpreter> {
    private val TAG = TfliteLoader::class.java.simpleName
    private val tfliteExeOption : Interpreter.Options

    constructor(context:Context, exeOption : ExecutionOption) : super(context) {
        val compatList = CompatibilityList()
        if (compatList.isDelegateSupportedOnThisDevice) {
            val delegateOptions = compatList.bestOptionsForThisDevice
            //gpuDelegate = GpuDelegate(delegateOptions)
        }

        val opt = convertOption(exeOption)?: Interpreter.Options().apply {
                if (compatList.isDelegateSupportedOnThisDevice) {
                    // if the device has a supported GPU, add the GPU delegate
                    /*gpuDelegate?.let{
                        this.addDelegate(gpuDelegate)
                    }*/
                } else {
                    // if the GPU is not supported, run on 4 threads
                    this.setNumThreads(4)
                }
            }
        tfliteExeOption = opt
    }

    override fun loadModel(modelName: String): Interpreter {
        return if(isAssetFile(LibAI.getApplicationContext().assets, modelName)) {
            Interpreter(FileUtil.loadMappedFile(LibAI.getApplicationContext(), modelName), tfliteExeOption)
        }else{
            Interpreter(ByteBuffer.wrap(File(modelName).readBytes()), tfliteExeOption)
        }
    }

    @Throws(RuntimeException::class)
    private fun convertOption(option : ExecutionOption?) : Interpreter.Options? {
        if(option == null)
            return null

        val interpreterOpt = Interpreter.Options()
        interpreterOpt.setNumThreads(option.numThread)

        when(option.delegate){
            ExecutionOption.DELEGATE.XNNPACK-> interpreterOpt.setUseXNNPACK(true)
            ExecutionOption.DELEGATE.NNAPI -> interpreterOpt.setUseNNAPI(true)
            ExecutionOption.DELEGATE.GPU -> {
                /*gpuDelegate?.let{
                    interpreterOpt.addDelegate(gpuDelegate)
                }*/
            }
            ExecutionOption.DELEGATE.CPU -> {}
        }
        return interpreterOpt
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}