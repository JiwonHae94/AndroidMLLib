package com.jiwon.lib_ai.model.core

import com.jiwon.lib_ai.model.loader.ModelLoader
import android.content.Context
import com.jiwon.lib_ai.model.RuntimeConfig

/**
 * Model is the base class for any type of models
 *
 * @param IType : Interpreter type of the model
 * @constructor Create empty Model
 */
abstract class Model<IType> {
    protected val context:Context

    protected val interpreter : IType

    protected val modelInfo : ModelInfo

    protected val runtimeConfig : RuntimeConfig

    protected val inputWidth : Int
        get() = inputShape.first()[1]

    protected val inputHeight : Int
        get() = inputShape.first()[2]

    protected val inputChannels : Int
        get() = inputShape.first()[3]

    protected val inputShape : Array<IntArray>
        get() = modelInfo.inputShape ?: getInputShapeFromModel()

    protected val outputShape : Array<IntArray>
        get() = modelInfo.outputShape?: getOutputShapeFromModel()

    abstract fun getInputShapeFromModel() : Array<IntArray>

    abstract fun getOutputShapeFromModel() : Array<IntArray>

    constructor(context:Context, modelInfo : ModelInfo, runtimeConfig : RuntimeConfig?){
        this.context = context
        this.modelInfo = modelInfo
        this.runtimeConfig = runtimeConfig ?: defaultRuntimeConfig
        this.interpreter = loadModel()

        initializeModel()
    }

    private val defaultRuntimeConfig = RuntimeConfig.Builder().build()

    protected abstract val modelLoader : ModelLoader<IType>

    protected abstract fun loadModel() : IType

    protected abstract fun run(vararg var1 : Any)

    protected abstract fun initializeModel()

    protected abstract fun processOutput()

}