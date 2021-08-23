package com.jiwon.lib_ai.model.support.image

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ColorChannelOperator(val channel: Conversion) : ImageOperator() {
    override fun apply(var1: Mat): Mat {
        var1.channels()
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