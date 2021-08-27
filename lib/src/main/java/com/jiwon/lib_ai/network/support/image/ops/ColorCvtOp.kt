package com.jiwon.lib_ai.network.support.image.ops

import com.jiwon.lib_ai.network.support.image.ColorChannel
import com.jiwon.lib_ai.network.support.image.NetImage
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.lang.IllegalStateException

class ColorCvtOp(val targetChannel: ColorChannel) : ImageOp() {
    override fun apply(var1: NetImage)  {
        val currentColorChannel = var1.colorChannel
        if(currentColorChannel == targetChannel)
            return

        val srcMat = var1.mat
        when(currentColorChannel){
            ColorChannel.RGB->{
                if(targetChannel == ColorChannel.BGR){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2BGR)
                }else if(targetChannel == ColorChannel.GRAYSCALE){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2GRAY)
                }else{
                    checkValidity(currentColorChannel, targetChannel)
                }
            }
            ColorChannel.BGR->{
                if(targetChannel == ColorChannel.BGR){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2BGR)
                }else if(targetChannel == ColorChannel.GRAYSCALE){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2GRAY)
                }else{
                    checkValidity(currentColorChannel, targetChannel)
                }
            }

            ColorChannel.GRAYSCALE->{

            }
            else -> checkValidity(currentColorChannel, targetChannel)
        }
    }

    private fun handleConversiion(srcMat : Mat, srcColorChannel: ColorChannel, targetColorChannel: ColorChannel){
        when(srcColorChannel){
            ColorChannel.RGB->{
                if(targetChannel == ColorChannel.BGR){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2BGR)
                }else if(targetChannel == ColorChannel.GRAYSCALE){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2GRAY)
                }else{
                    checkValidity(srcColorChannel, targetChannel)
                }
            }
            ColorChannel.BGR->{
                if(targetChannel == ColorChannel.RGB){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGR2RGB)
                }else if(targetChannel == ColorChannel.GRAYSCALE){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_BGR2GRAY)
                }else{
                    checkValidity(srcColorChannel, targetChannel)
                }
            }
            ColorChannel.GRAYSCALE->{
                if(targetChannel == ColorChannel.RGB){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_GRAY2RGB)
                }else if(targetChannel == ColorChannel.BGR){
                    Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_GRAY2BGR )
                }else{
                    checkValidity(srcColorChannel, targetChannel)
                }
            }
        }
    }

    private fun checkValidity(srcChannel : ColorChannel, dstColorChannel: ColorChannel){
        require(srcChannel == ColorChannel.RGB || srcChannel == ColorChannel.BGR || srcChannel == ColorChannel.GRAYSCALE)
        require(dstColorChannel == ColorChannel.RGB || dstColorChannel == ColorChannel.BGR || dstColorChannel == ColorChannel.GRAYSCALE)
    }
}