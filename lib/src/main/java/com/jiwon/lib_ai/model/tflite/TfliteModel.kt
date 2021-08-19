package com.jiwon.lib_ai.model.tflite

import com.jiwon.lib_ai.model.core.Model
import com.jiwon.lib_ai.model.core.ModelInfo
import android.content.Context
import com.jiwon.lib_ai.model.RuntimeConfig
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.lang.IllegalArgumentException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.jiwon.lib_ai.model.loader.TfliteLoader
import org.tensorflow.lite.DataType

abstract class TfliteModel<ModelOutputT> : Model<Interpreter> {
    private val TAG = TfliteModel::class.java.simpleName

    final override val modelLoader: TfliteLoader

    protected val inferenceOutput : HashMap<Int, Any> = HashMap()

    final override fun getInputShapeFromModel(): Array<IntArray> {
        val shape = MutableList(interpreter.inputTensorCount){ intArrayOf() }
        for(i in 0 until interpreter.inputTensorCount){
            shape[i] = interpreter.getInputTensor(i).shape()
        }
        return shape.toTypedArray()
    }

    final override fun getOutputShapeFromModel(): Array<IntArray> {
        val shape = MutableList(interpreter.outputTensorCount){ intArrayOf() }
        for(i in 0 until interpreter.outputTensorCount){
            shape[i] = interpreter.getOutputTensor(i).shape()
        }
        return shape.toTypedArray()
    }

    constructor(context:Context, modelInfo: ModelInfo, runtimeConfig: RuntimeConfig) : super(context, modelInfo, runtimeConfig){
        this.modelLoader = TfliteLoader(context)
    }

    override fun loadModel(): Interpreter {
        return modelLoader.loadAssetModel(modelInfo.modelName, runtimeConfig)
    }

    /**
     * Sets default hash map to hold inference result from the model
     * Some models does not have its tensors appropriately configured, developer may be required to configure them manually
     */
    @Throws(IllegalArgumentException::class)
    protected open fun configureInferenceOutput(){
        /*val outputShape = modelInfo.outputShape ?: return defaultConfiguration()

        require(modelInfo.dataType != null){ "model info must be appropriately set" }

        val outputBuffer = getTensorOutput(outputShape, convertDataType(modelInfo.dataType!!))
        outputBuffer?.let{} ?: throw IllegalArgumentException("invalid input shape is given, please check with the model developer")

        inferenceOutput.put(0, outputBuffer)*/
    }

    protected fun convertDataType(dataType: com.jiwon.lib_ai.model.DataType) : DataType {
        return when(dataType){
            com.jiwon.lib_ai.model.DataType.FLOAT32 -> DataType.FLOAT32
            com.jiwon.lib_ai.model.DataType.INT32   -> DataType.INT32
            com.jiwon.lib_ai.model.DataType.UINT8   -> DataType.UINT8
            com.jiwon.lib_ai.model.DataType.INT64   -> DataType.INT64
            com.jiwon.lib_ai.model.DataType.STRING  -> DataType.STRING
            com.jiwon.lib_ai.model.DataType.INT8    -> DataType.INT8
        }
    }

    private fun defaultConfiguration(){
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