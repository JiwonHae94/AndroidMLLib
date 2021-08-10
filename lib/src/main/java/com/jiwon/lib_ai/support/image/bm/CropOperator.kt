package com.jiwon.lib_ai.support.image.bm

import android.graphics.Bitmap
import com.jiwon.lib_ai.support.image.ImageOperator

class CropOperator(val param : Any) : ImageOperator() {
    override fun apply(var1: Bitmap): Bitmap {
        if(param is Int)
            return cropBitmap(var1, param, param)
        return var1
    }

    private fun cropBitmap(bm : Bitmap, width : Int, height : Int) : Bitmap{
        var x = 0
        var y = 0

        if(bm.width > width) x = (bm.width - width) / 2
        if(bm.height > height) y = (bm.height - height) / 2

        var cw: Int = width // crop width
        var ch: Int = height // crop height

        if (width > bm.width) cw = width
        if (height > bm.height) ch = height

        return Bitmap.createBitmap(bm, x, y, cw, ch)
    }


}