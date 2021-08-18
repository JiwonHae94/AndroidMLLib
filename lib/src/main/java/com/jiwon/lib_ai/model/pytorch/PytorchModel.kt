package com.jiwon.lib_ai.model.pytorch

import android.content.Context
import com.jiwon.lib_ai.model.core.Model
import com.jiwon.lib_ai.model.core.ModelInfo
import com.jiwon.lib_ai.model.loader.ModelLoader
import com.jiwon.lib_ai.model.exe.ExecutionOption
import org.pytorch.Module
import org.pytorch.Tensor
import java.nio.FloatBuffer

abstract class PytorchModel<ModelOutputT> : Model<Module> {
    private val context:Context

    protected val modelInfo : ModelInfo

    final override val interpreter : Module

    final override val modelLoader : ModelLoader

    protected val tensorBuffer : FloatBuffer
    protected val tensor : Tensor

    override val inputShape: IntArray

    constructor(context: Context, modelInfo : ModelInfo, exeOption : ExecutionOption) : super(){
        this.context = context
        this.modelInfo = modelInfo
        this.inputShape = modelInfo.inputShape ?: intArrayOf(-1, -1)
        this.modelLoader = ModelLoader(context)
        this.interpreter = loadModel(modelInfo)

        this.tensorBuffer = Tensor.allocateFloatBuffer(3 * inputShape[0] * inputShape[1])
        this.tensor = Tensor.fromBlob(tensorBuffer, longArrayOf(1, 3, inputShape[0].toLong(), inputShape[1].toLong()))
    }


    protected open fun configureInferenceOutput(){

    }

    protected abstract fun processInput(vararg var1 : Any) : Tensor
}