package com.jiwon.lib_ai.model.loader

import org.pytorch.Module
import android.content.Context
import com.jiwon.lib_ai.Utility
import com.jiwon.lib_ai.model.RuntimeConfig
import org.pytorch.LiteModuleLoader
import org.pytorch.PyTorchAndroid
import java.io.File

class PytorchLoader : ModelLoader<Module> {
    constructor(context:Context) : super(context)

    private fun initLoadOption(option : RuntimeConfig?){
        val opt = option ?: RuntimeConfig.Builder().build()
        PyTorchAndroid.setNumThreads(opt.numThread)
    }

    override fun loadAssetModel(modelName: String, loadOption: RuntimeConfig?): Module {
        initLoadOption(loadOption)
        val modelAddr = Utility.getAssetAddress(fileName = modelName)

        return try{
            Module.load(modelAddr)
        }catch(e:Exception){
            LiteModuleLoader.load(modelAddr)
        }
    }

    override fun loadModel(path : String, modelName: String, loadOption : RuntimeConfig?): Module {
        initLoadOption(loadOption)

        val modelAddr = File(path, modelName).absolutePath

        return try{
            Module.load(modelAddr)
        }catch(e:Exception){
            LiteModuleLoader.load(modelAddr)
        }
    }
}