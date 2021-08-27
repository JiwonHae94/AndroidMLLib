package com.jiwon.lib_ai.network.support.image.ops

import com.jiwon.lib_ai.network.support.image.NetImage
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ResizeOp(val size : Size, val type : Int = Imgproc.INTER_CUBIC) : ImageOp() {
    override fun apply(var1: NetImage) {
        val newMat = var1.mat
        Imgproc.resize(newMat, newMat, size, 0.0, 0.0, type)
        return var1.load(newMat)
    }
}