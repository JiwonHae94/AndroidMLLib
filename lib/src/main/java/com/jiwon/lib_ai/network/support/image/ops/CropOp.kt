package com.jiwon.lib_ai.network.support.image.ops

import com.jiwon.lib_ai.network.support.image.NetImage
import org.opencv.core.Mat
import org.opencv.core.Rect


class CropOp(val roi : Rect) : ImageOp() {
    override fun apply(var1: NetImage) {
        val croppedMat = Mat(var1.mat, roi)
        var1.load(croppedMat)
    }
}