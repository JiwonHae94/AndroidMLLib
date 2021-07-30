package com.jiwon.lib_ai.support.image.mat

import android.graphics.Bitmap
import com.jiwon.lib_ai.support.core.Operator
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ColorChannelOperator(val channel: Conversion) : Operator<Mat, Mat> {
    override fun apply(var1: Mat): Mat {
        val conv = when(channel){
            Conversion.RGB2BGR -> Imgproc.COLOR_RGB2BGR
            Conversion.BGR2RGB -> Imgproc.COLOR_BGR2RGB
            Conversion.RGB2GRAY -> Imgproc.COLOR_RGB2GRAY
            Conversion.BGR2GRAY -> Imgproc.COLOR_BGR2GRAY
        }
        val dst = Mat()
        Imgproc.cvtColor(var1, dst, conv)
        return dst
    }

    enum class Conversion{
        RGB2BGR, BGR2RGB, RGB2GRAY, BGR2GRAY
    }
}