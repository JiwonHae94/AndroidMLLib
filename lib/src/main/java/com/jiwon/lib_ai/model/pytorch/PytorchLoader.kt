package com.jiwon.lib_ai.model.pytorch

import com.jiwon.lib_ai.model.core.ModelLoader
import com.jiwon.lib_ai.model.exe.ExecutionOption
import android.content.Context
import com.jiwon.lib_ai.Utility
import org.pytorch.Module

class PytorchLoader : ModelLoader<Module> {
    constructor(context:Context, opt : ExecutionOption) :super(context){
        when(opt.delegate){

        }
    }

    override fun loadModel(modelName: String): Module {
        return Module.load(Utility.getAssetAddress(fileName = modelName))
    }

    override fun close() {
        //
    }

}