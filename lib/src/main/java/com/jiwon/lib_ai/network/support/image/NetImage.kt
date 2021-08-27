package com.jiwon.lib_ai.network.support.image

import android.graphics.Bitmap
import com.jiwon.lib_ai.network.DataType
import com.jiwon.lib_ai.network.DataType.Companion.toTfliteType
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.tensorflow.lite.support.image.TensorImage

class NetImage {
    private val container : ImageContainer

    internal val netBuffer : NetBuffer?
        get() = this.container.bufferImage

    internal val bitmap : Bitmap
        get() = this.container.bitmap

    internal val mat : Mat
        get() = this.container.mat

    internal val dataType : DataType
        get() = this.container.dataType

    internal val width : Int
        get() = this.container.width

    internal val height : Int
        get() = this.container.height

    internal val colorChannel : ColorChannel
        get() = this.container.colorChannel


    constructor(dataType : DataType = DataType.UINT8){
        require(dataType == DataType.UINT8 || dataType == DataType.FLOAT32){"Illegal data type for NetImage type"  }
        this.container = ImageContainer(dataType)
    }

    internal fun load(var1 : Bitmap?){
        var1 ?: throw IllegalStateException("NetImage cannot load null bitmap")
        require(var1.config.equals(Bitmap.Config.ARGB_8888)) { "Only supported ARGB_8888 bitmaps"}
        this.container.set(var1)
    }

    internal fun load(pixels : FloatArray, shape : IntArray){
        checkImageNetShape(shape)
        val netBuffer = NetBuffer.createDynamic(dataType = this.dataType)
        netBuffer.loadArray(pixels, shape)
        this.load(netBuffer)
    }

    internal fun load(var1 : Mat, colorChannel: ColorChannel? = null){
        this.container.set(var1, colorChannel)
    }

    internal fun load(pixels : IntArray, shape : IntArray){
        checkImageNetShape(shape)
        val netBuffer = NetBuffer.createDynamic(dataType = this.dataType)
        netBuffer.loadArray(pixels, shape)
        this.load(netBuffer)
    }

    fun load(buffer: NetBuffer) {
        checkImageNetShape(buffer.shape)
        container.set(buffer)
    }

    private class ImageContainer{
        private var bitmapImage : Bitmap? = null
        private var matImage : Mat? = null
        internal var colorChannel = ColorChannel.RGB

        internal val bitmap : Bitmap
            get() = getBitmap()

        internal val mat : Mat
            get() = matImage ?: let{
                val srcMat = Mat()
                Utils.bitmapToMat(getBitmap(), srcMat)
                srcMat
            }


        internal var bufferImage : NetBuffer? = null
        internal var isBitmapUpdated : Boolean = false
        internal var isBufferUpdated : Boolean = false
        internal var isMatUpdated : Boolean = matImage != null
        internal val dataType : DataType

        constructor(dataType : DataType){
            this.dataType = dataType
        }

        val width : Int
            get(){
                require(isBitmapUpdated || isBufferUpdated){"Both buffer and bitmap data are obsolete. Forgot to call NetImage#load?"}
                return if (isBitmapUpdated) this.bitmapImage!!.width else this.getBufferDimensionSize(-2)
            }

        val height : Int
            get(){
                require(isBitmapUpdated || isBufferUpdated){"Both buffer and bitmap data are obsolete. Forgot to call NetImage#load?"}
                return if (isBitmapUpdated) this.bitmapImage!!.height else this.getBufferDimensionSize(-2)
            }

        private fun getBitmap() : Bitmap{
            if(isMatUpdated){
                val srcBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(mat, srcBitmap)
                return srcBitmap
            }

            if(isBufferUpdated)
                return bitmapImage!!
            else if(!this.isBufferUpdated || this.bufferImage == null)
                throw IllegalStateException("Both buffer and bitmap data are obsolete. Forgot to call NetImage#load?")
            else if(bufferImage?.dataType != DataType.UINT8)
                throw IllegalStateException("NetImage is holding a float-value image which is not able to convert a Bitmap")
            else{
                return createBitmapFromBuffer()
            }
        }

        private fun createBitmapFromBuffer() : Bitmap{
            val requiredAllocation = this.bufferImage!!.flatSize * 4
            if (bitmapImage == null || bitmapImage!!.allocationByteCount < requiredAllocation) {
                val shape: IntArray = bufferImage!!.shape
                val h = shape[shape.size - 3]
                val w = shape[shape.size - 2]
                bitmapImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            }
            this.isBitmapUpdated = true
            return bitmapImage!!
        }

        private fun createMat(bm : Bitmap) : Mat{
            val srcMat = Mat()
            Utils.bitmapToMat(bm, srcMat)
            return srcMat
        }


        private fun getBufferDimensionSize(dim: Int): Int {
            bufferImage?: throw NullPointerException("buffer image is obsolete")

            var dim = dim
            val shape: IntArray = this.bufferImage!!.shape
            checkImageNetShape(shape)

            dim %= shape.size
            if (dim < 0) {
                dim += shape.size
            }
            return shape[dim]
        }


        fun set(var1:Mat, colorChannel: ColorChannel? = null){
            this.isMatUpdated = true
            this.colorChannel = colorChannel ?: ColorChannel.BGR
            this.matImage = var1

            val srcBitmap = Bitmap.createBitmap(var1.width(), var1.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(var1, srcBitmap)
            set(srcBitmap, colorChannel)
        }

        fun set(var1 : Bitmap?, colorChannel: ColorChannel? = null){
            this.colorChannel = colorChannel?: ColorChannel.RGB

            this.bitmapImage = var1
            this.isBitmapUpdated= true
            this.isBufferUpdated = false
        }


        fun set(buffer : NetBuffer, colorChannel: ColorChannel? = null){
            this.colorChannel = colorChannel ?: ColorChannel.RGB
            this.bufferImage = buffer
            this.isBitmapUpdated = false
            this.isBufferUpdated = true
        }

        companion object{
            private const val ARGB_8888_ELEMENT_BYTES = 4
        }
    }

    companion object{
        fun fromBitmap(var1 : Bitmap) : NetImage {
            val inferenceImage = NetImage()
            inferenceImage.load(var1)
            return inferenceImage
        }

        fun createFrom(src : NetImage, dataType: DataType) : NetImage {
            val dst = NetImage(dataType)
            if(src.container.isBufferUpdated)
                dst.container.set(NetBuffer.createFrom(src.netBuffer, dataType))
            else if(src.container.isBitmapUpdated){
                val srcBitmap = src.bitmap
                dst.container.set(srcBitmap?.copy(srcBitmap.config, srcBitmap.isMutable))
            }
            return dst
        }

        @Throws(IllegalArgumentException::class)
        fun checkImageNetShape(shape : IntArray){
            require((shape.size == 3 || shape.size == 4 && shape[0] == 1) && shape[shape.size - 3] > 0 && shape[shape.size - 2] > 0 && shape[shape.size - 1] == 3){ "Only supports image shape in (h, w, c) or (1, h, w, c), and channels representing R, G, B in order."}
        }

        fun NetImage.toTensorImage() : TensorImage {
            val tensorImage = TensorImage(this.dataType.toTfliteType())
            tensorImage.load(this.bitmap)
            return tensorImage
        }
    }

}