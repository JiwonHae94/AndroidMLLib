package com.jiwon.lib_ai.model.support.image

import android.graphics.Bitmap
import com.jiwon.lib_ai.model.support.core.Operator
import org.opencv.android.Utils
import org.opencv.core.Mat

abstract class MatrixOperator : Operator<Mat, Mat> {

    companion object{
        fun Bitmap.toMat() : Mat{
            val srcMat = Mat()
            Utils.bitmapToMat(this, srcMat)
            return srcMat
        }
    }
}