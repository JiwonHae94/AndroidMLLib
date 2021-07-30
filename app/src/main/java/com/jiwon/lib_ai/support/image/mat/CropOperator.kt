package com.jiwon.lib_ai.support.image.mat

import com.jiwon.lib_ai.support.image.MatrixOperator
import org.opencv.core.Mat
import org.opencv.core.Rect


class CropOperator(val roi : Rect) : MatrixOperator() {
    override fun apply(var1: Mat): Mat {
        return Mat(var1, roi)
    }
}