package com.jiwon.lib_ai.model.support

import android.graphics.Bitmap
import com.jiwon.lib_ai.model.core.DataType
import com.jiwon.lib_ai.model.core.DataType.Companion.toTfliteType
import com.jiwon.lib_ai.model.core.InterpreterType
import com.jiwon.lib_ai.model.support.image.ImageOperator
import com.jiwon.lib_ai.model.support.image.ImageOperator.Companion.toBitmap
import com.jiwon.lib_ai.model.support.image.ImageOperator.Companion.toMat
import org.tensorflow.lite.support.image.TensorImage
import java.lang.IllegalArgumentException
import java.nio.ByteOrder

class ImageProcessor private constructor(val builder : Builder) {
    private val iamgeProcessSequence = builder.processSequence.toTypedArray()
    private val pixelProcessEquence = builder.others.toTypedArray()

    /**
     * Loops over bitmap operation given by the user to preprocess bitmap
     * @see bitmapOperatorList contains raw operation
     * @see tfliteOperatorList contains preprocess operations that tflite provides
     *
     * @param bm : raw bitmap
     * @return preprocessed bitmap appropriate for inference
     */
    internal fun process(bm : Bitmap){
        var srcMat = bm.toMat()
        for(ops in iamgeProcessSequence){
            srcMat = ops.apply(srcMat)
        }

        val bm = srcMat.toBitmap()
        val input = when(builder.platform){
            InterpreterType.tflite -> convert<TensorImage>(bm)
            else -> IllegalArgumentException("currently not supported")
        }

        for(ops in pixelProcessEquence){
            ops.apply(input)
        }
    }

    private fun <T> convert(bm : Bitmap) : T{
        return when(builder.platform){
            InterpreterType.tflite -> {
                val tensorImage = TensorImage(builder.datatype.toTfliteType())
                tensorImage.load(bm)
                tensorImage.buffer.order(ByteOrder.nativeOrder())
            }
            InterpreterType.pytorch ->{
                throw IllegalArgumentException("pytorch is not supported at the moment")
            }
        } as T
    }


    class Builder(val platform : InterpreterType, val datatype : DataType){
        internal val processSequence = ArrayList<ImageOperator>()
        internal val others = ArrayList<Operator<Any, Any>>()

        fun add(op : Operator<Any, Any>) : Builder {
            when(op){
                is ImageOperator-> processSequence.add(op)
                else -> others.add(op)
            }
            return this
        }

        fun build() : ImageProcessor {
            return ImageProcessor(this)
        }
    }
}