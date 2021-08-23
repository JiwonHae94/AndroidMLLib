package com.jiwon.lib_ai.model.support.common

import com.jiwon.lib_ai.model.pytorch.TorchBuffer
import com.jiwon.lib_ai.model.support.Operator
import org.pytorch.MemoryFormat
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.SupportPreconditions
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat

class NormalizeOp<T> : Operator<T, T> {
    private val mean : FloatArray
    private val stddev : FloatArray
    private val numChannels : Int
    private var allMeansAreZeroAndAllDevsAre1 = true
    private var isIdentityOp = allMeansAreZeroAndAllDevsAre1

    private val memoryFormat : MemoryFormat

    /**
     * @param mean : mean of the bitmap
     * @param stddev : standard deivatoin of the input
     * @param memoryFormat : only required for pytorch
     */
    constructor(mean : Float, std : Float, memoryFormat : MemoryFormat = MemoryFormat.CONTIGUOUS) : this(floatArrayOf(mean), floatArrayOf(std) , memoryFormat)

    constructor(mean : FloatArray, std: FloatArray, memoryFormat : MemoryFormat) : super(){
        this.mean = mean.clone()
        this.stddev = std.clone()
        this.numChannels = this.mean.size
        this.memoryFormat = memoryFormat

        for (i in 0 until numChannels) {
            SupportPreconditions.checkArgument(this.stddev.get(i) != 0.0f, "Stddev cannot be zero.")

            if (this.stddev.get(i) != 1.0f || this.mean[i] != 0.0f) {
                allMeansAreZeroAndAllDevsAre1 = false
            }
        }

        this.isIdentityOp = allMeansAreZeroAndAllDevsAre1;
    }

    override fun apply(var1: T): T {
        return when(var1){
            is TensorBuffer -> tfNormalizeOp(var1) as T
            //is TorchBuffer  -> torchNomalizeOp(var1) as T
            else -> throw IllegalArgumentException("operation is currently not supported")
        }
    }

    /*private fun torchNomalizeOp(buffer : TorchBuffer) : TorchBuffer{
        val shape = buffer.shape

        SupportPreconditions.checkArgument(
            this.numChannels == 1 || shape.size != 0 && shape[shape.size - 1] == this.numChannels,
            "Number of means (stddevs) is not same with number of channels (size of last axis)."
        )
        val values = buffer.floatArray
        var i = 0
        val pixelCount = values.size

        if(memoryFormat == MemoryFormat.CONTIGUOUS){
            val offsetG = pixelCount
            val offsetB = 2 * pixelCount

            for(i in 0 until pixelCount){
                val pixel = values.get(i)

                val r: Float = (pixel.toInt() shr 16 and 0xff) / 255.0f
                val g: Float = (pixel.toInt() shr 8 and 0xff) / 255.0f
                val b: Float = (pixel.toInt() and 0xff) / 255.0f

                values.put(outBufferOffset + i, (r - normMeanRGB.get(0)) / normStdRGB.get(0))
                outBuffer.put(
                    outBufferOffset + offset_g + i,
                    (g - normMeanRGB.get(1)) / normStdRGB.get(1)
                )
                outBuffer.put(
                    outBufferOffset + offset_b + i,
                    (b - normMeanRGB.get(2)) / normStdRGB.get(2)
                )

            }

        }else{

        }

    }*/

    private fun tfNormalizeOp(buffer : TensorBuffer) : TensorBuffer{
        val shape = buffer.shape
        SupportPreconditions.checkArgument(
            this.numChannels == 1 || shape.size != 0 && shape[shape.size - 1] == this.numChannels,
            "Number of means (stddevs) is not same with number of channels (size of last axis)."
        )

        val values = buffer.floatArray
        var j = 0
        for(i in 0 until values.size){
            values[i] = (values[i] - this.mean[j]) / this.stddev[j];
            j = (j + 1) % this.numChannels;
        }

        val output : TensorBuffer = if(buffer.isDynamic) TensorBufferFloat.createDynamic(DataType.FLOAT32) else TensorBufferFloat.createFixedSize(shape, DataType.FLOAT32)
        output.loadArray(values, shape)
        return output

    }

}