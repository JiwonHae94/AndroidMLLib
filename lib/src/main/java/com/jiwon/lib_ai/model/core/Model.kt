package com.jiwon.lib_ai.model.core

import com.jiwon.lib_ai.model.listener.ModelInferenceListener
import com.jiwon.lib_ai.model.loader.ModelLoader

/**
 * Model is the base class for any type of models
 *
 * @param IType : Interpreter type of the model
 * @constructor Create empty Model
 */
abstract class Model<IType> {
    abstract protected val interpreter : IType
    protected abstract val modelLoader : ModelLoader<IType>
    protected var modelInferenceListener : ModelInferenceListener? = null

    protected open fun loadModel(modelInfo : ModelInfo) : IType{
        return modelLoader.load(modelName = modelInfo.modelName)
    }

    protected abstract fun run(vararg var1 : Any)

    protected abstract val inputShape : IntArray

    protected abstract fun initializeModel()
    protected abstract fun processOutput()

    internal fun addOnModelInferenceListener(var1 : ModelInferenceListener) : Model<IType>{
        this.modelInferenceListener = var1
        return this
    }
}