package com.jiwon.lib_ai.support.image.bm

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.util.Size
import com.jiwon.lib_ai.support.image.ImageOperator

class ResizeOperator(val param : Any) : ImageOperator() {
    private val TAG = ResizeOperator::class.java.simpleName

    override fun apply(var1: Bitmap): Bitmap {
        try{
            if(param is Float){
                return resizeWithSF(var1, param)
            } else if(param is Size){
                return Bitmap.createScaledBitmap(var1, param.width, param.height, false)
            }
        }catch(e: Exception){
            Log.e(TAG, e.stackTraceToString())
        }
        return var1
    }

    private fun resizeWithSF(var1 : Bitmap, sf : Float) : Bitmap {
        val width: Int = var1.width
        val height: Int = var1.height
        val matrix = Matrix()
        matrix.postScale(sf, sf)

        return Bitmap.createBitmap(var1, 0, 0, width, height, matrix, true)
    }
}