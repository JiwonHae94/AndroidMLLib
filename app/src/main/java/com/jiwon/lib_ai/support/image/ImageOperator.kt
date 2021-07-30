package com.jiwon.lib_ai.support.image

import android.graphics.Bitmap
import com.jiwon.lib_ai.support.core.Operator
import org.opencv.android.Utils
import org.opencv.core.Mat

abstract class ImageOperator : Operator<Bitmap, Bitmap>{
    companion object{
        fun Mat.toBitmap() : Bitmap{
            val bm = Bitmap.createBitmap(this.width(), this.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(this, bm)
            return bm
        }
    }
}