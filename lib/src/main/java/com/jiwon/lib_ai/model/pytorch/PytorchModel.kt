package com.jiwon.lib_ai.model.pytorch

import android.content.Context
import com.jiwon.lib_ai.model.core.Model
import com.jiwon.lib_ai.model.core.ModelInfo
import com.jiwon.lib_ai.model.RuntimeConfig
import com.jiwon.lib_ai.model.loader.PytorchLoader
import org.pytorch.Module
import org.pytorch.Tensor

abstract class PytorchModel<ModelOutputT> : Model<Module> {
    final override val modelLoader : PytorchLoader

    override val inputShape: IntArray

    constructor(context: Context, modelInfo : ModelInfo, runtimeConfig : RuntimeConfig) : super(context, modelInfo, runtimeConfig){
        this.inputShape = modelInfo.inputShape ?: intArrayOf(-1, -1)
        this.modelLoader = PytorchLoader(context)
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