package com.jiwon.lib_ai.support.image.mat

import com.jiwon.lib_ai.support.image.MatrixOperator
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ResizeOperator(val size : Size) : MatrixOperator() {
    override fun apply(var1: Mat): Mat {
        Imgproc.resize(var1, var1, size)
        return var1
    }
}