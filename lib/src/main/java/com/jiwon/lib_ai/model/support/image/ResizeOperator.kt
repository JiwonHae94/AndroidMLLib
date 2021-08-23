package com.jiwon.lib_ai.model.support.image

import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ResizeOperator(val size : Size, val type : Int = Imgproc.INTER_CUBIC) : ImageOperator() {
    override fun apply(var1: Mat): Mat {
        Imgproc.resize(var1, var1, size, 0.0, 0.0, type)
        return var1
    }
}