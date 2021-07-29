package com.jiwon.lib_ai.model.tflite

import com.jiwon.lib_ai.model.core.Model
import com.jiwon.lib_ai.model.core.ModelInfo
import android.content.Context
import com.jiwon.lib_ai.model.core.ModelLoader
import com.jiwon.lib_ai.model.exe.ExecutionOption
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.lang.IllegalArgumentException
import android.util.Log

abstract class TfliteModel<ModelOutputT> : Model<Interpreter> {
    private val TAG = TfliteModel::class.java.simpleName

    private val context: Context
    private val exeOption: ExecutionOption
    private val modelInfo : ModelInfo

    final override val interpreter: Interpreter
    final override val modelLoader: ModelLoader<Interpreter>

    protected val inferenceOutput : HashMap<Int, Any> = HashMap()

    final override val inputShape : IntArray get(){
        return modelInfo.inputShape ?: interpreter.getInputTensor(0).shape()
    }

    constructor(context:Context, modelInfo: ModelInfo, exeOption: ExecutionOption) : super(){
        this.context = context
        this.modelInfo = modelInfo
        this.exeOption = exeOption
        this.modelLoader = TfliteLoader(context, exeOption)
        this.interpreter = loadModel(modelInfo)
    }

    /**
     * Default configuration of the output hashmap
     *
     */
    @Throws(IllegalArgumentException::class)
    protected open fun configureInferenceOutput(){
        val outputTensor = interpreter.getOutputTensor(0)
        val outputType = outputTensor.dataType()
        val outputShape = outputTensor.shape()

        val outputBuffer = getTensorOutput(outputShape, outputType)
        outputBuffer?.let{} ?: throw IllegalArgumentException("invalid input shape is given, please check with the model developer")

        inferenceOutput.put(0, outputBuffer)
    }

    override fun initializeModel() {
        configureInferenceOutput()
    }

    protected abstract fun preprocessInput(vararg var1 : Any) : TensorImage

    final override fun run(vararg var1 : Any){
        try{
            val modelInput = preprocessInput(var1)
            interpreter.runForMultipleInputsOutputs(arrayOf(modelInput.buffer.rewind()), inferenceOutput)
        }catch(e: Exception){
            Log.e(TAG, e.stackTraceToString())
        }
    }

    override fun toString(): String {
        val strBuilder = StringBuilder(modelInfo.modelName)
        return strBuilder.toString()
    }

    companion object{
        fun getTensorOutput(size : IntArray, dataType : DataType) : TensorBuffer? {
            return try{
                TensorBuffer.createFixedSize(size, dataType)
            }catch(e: Exception){
                null
            }
        }
    }

}