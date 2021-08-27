package com.jiwon.lib_ai.network.pytorch

import android.content.Context
import com.jiwon.lib_ai.network.core.Model
import com.jiwon.lib_ai.network.core.ModelInfo
import com.jiwon.lib_ai.network.RuntimeConfig
import com.jiwon.lib_ai.network.loader.PytorchLoader
import org.pytorch.Module
import org.pytorch.Tensor

abstract class PytorchModel<ModelOutputT> : Model<Module> {
    final override val modelLoader : PytorchLoader

    constructor(context: Context, modelInfo : ModelInfo, runtimeConfig : RuntimeConfig) : super(context, modelInfo, runtimeConfig){
        this.modelLoader = PytorchLoader(context)
    }

    final override fun getInputShapeFromModel(): Array<IntArray> {
        throw IllegalArgumentException("Pytorch does not support getting shape from the model. Parameters must be set manually")
    }

    final override fun getOutputShapeFromModel(): Array<IntArray> {
        throw IllegalArgumentException("Pytorch does not support getting shape from the model. Parameters must be set manually")
    }

    /**
     * Load model from the asset folder
     * Method can be overridden to use different path
     * @return pytorch module
     */
    override fun loadModel(): Module {
        return modelLoader.loadAssetModel(modelInfo.modelName, runtimeConfig)
    }

    protected open fun configureInferenceOutput(){

    }

    protected abstract fun processInput(vararg var1 : Any) : Tensor
}