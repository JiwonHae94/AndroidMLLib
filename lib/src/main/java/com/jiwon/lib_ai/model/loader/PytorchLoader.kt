package com.jiwon.lib_ai.model.loader

import org.pytorch.Module
import android.content.Context
import com.jiwon.lib_ai.Utility
import org.pytorch.LiteModuleLoader
import org.pytorch.PyTorchAndroid

class PytorchLoader : ModelLoader<Module> {
    private constructor(context:Context) : super(context)

    private fun initLoadOption(option : LoadOption){
        PyTorchAndroid.setNumThreads(option.numThread)
    }

    override fun loadAssetModel(modelName: String): Module {
        return PyTorchAndroid.loadModuleFromAsset(context.assets, modelName)
    }

    override fun loadModel(modelName: String, loadOption : LoadOption?): Module {
        val loadOpt = loadOption ?: LoadOption.Builder().build()
        initLoadOption(loadOpt)

        val modelAddr = Utility.getAssetAddress(fileName = modelName)

        return try{
            Module.load(modelAddr)
        }catch(e:Exception){
            LiteModuleLoader.load(modelAddr)
        }
    }


}