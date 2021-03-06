package com.jiwon.lib_ai.support.image.bm

import android.graphics.Bitmap
import com.jiwon.lib_ai.support.core.Operator
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ColorChannelOperator(val channel: Conversion) : Operator<Bitmap, Bitmap> {
    override fun apply(var1: Bitmap): Bitmap {
        val conv = when(channel){
            Conversion.RGB2BGR -> Imgproc.COLOR_RGB2BGR
            Conversion.BGR2RGB -> Imgproc.COLOR_BGR2RGB
            Conversion.RGB2GRAY -> Imgproc.COLOR_RGB2GRAY
            Conversion.BGR2GRAY -> Imgproc.COLOR_BGR2GRAY
        }

        val src = Mat(var1.width, var1.height, CvType.CV_8UC4)
        Utils.bitmapToMat(var1, src)

        val dst = Mat(var1.width, var1.height, CvType.CV_8UC4)
        Imgproc.cvtColor(src, dst, conv)

        val bitmap = Bitmap.createBitmap(
            dst.width(),
            dst.height(),
            Bitmap.Config.ARGB_8888
        )

        Utils.matToBitmap(dst, bitmap)
        return bitmap
    }

    enum class Conversion{
        RGB2BGR, BGR2RGB, RGB2GRAY, BGR2GRAY
    }
}