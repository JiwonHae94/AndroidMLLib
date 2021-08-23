package com.jiwon.lib_ai.model.support.image

import android.graphics.Bitmap
import com.jiwon.lib_ai.model.support.Operator
import org.opencv.android.Utils
import org.opencv.core.Mat

abstract class ImageOperator : Operator<Mat, Mat> {
    companion object{
        fun Bitmap.toMat() : Mat{
            val srcMat = Mat()
            Utils.bitmapToMat(this, srcMat)
            return srcMat
        }

        fun Mat.toBitmap() : Bitmap{
            val outBm = Bitmap.createBitmap(this.width(), this.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(this, outBm)
            return outBm
        }
    }
}