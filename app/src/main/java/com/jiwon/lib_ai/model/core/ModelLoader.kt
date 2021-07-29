package com.jiwon.lib_ai.model.core

import android.content.res.AssetManager
import java.io.IOException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.jiwon.lib_ai.LibAi
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import android.content.Context
/**
 * Model loader loads AI model
 *
 * @param ModelT : Output model type. Type varies with platform
 * @constructor Create empty Model loader
 */
abstract class ModelLoader<ModelT>(protected val context:Context){
    internal abstract fun loadModel(modelName : String) : ModelT

    @Throws(IOException::class)
    protected fun load(modelName : String) : ByteArray {
        return loadFromBytes(modelName)
    }

    internal fun loadAssetFromInputStream(modelName: String) : InputStream {
        return context.resources.assets.open(modelName)
    }

    private fun loadFromExternalStorage(modelName: String) : InputStream {
        return FileInputStream(File(modelName))
    }

    private fun loadFromBytes(modelName : String) : ByteArray{
        val inputStream: InputStream =
            if(isAssetFile(context.assets, modelName)) {
                loadAssetFromInputStream(modelName)
            } else {
                loadFromExternalStorage(modelName)
            }

        val data = ByteArray(inputStream.available())
        inputStream.read(data)
        return data
    }

    fun isAssetFile(assetManager: AssetManager, fileName: String): Boolean{
        return try {
            assetManager.open(fileName)
            true
        }catch (e: IOException){
            false
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    abstract fun close()
}