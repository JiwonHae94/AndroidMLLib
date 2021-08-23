package com.jiwon.lib_ai.model.support.image

import org.opencv.core.Mat
import org.opencv.core.Rect


class CropOperator(val roi : Rect) : ImageOperator() {
    override fun apply(var1: Mat): Mat {
        return Mat(var1, roi)
    }
}