package com.jiwon.lib_ai.network.support.image

import com.jiwon.lib_ai.network.DataType
import java.lang.NullPointerException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

abstract class NetBuffer {
    var buffer: ByteBuffer? = null
    protected lateinit var _shape: IntArray

    val shape : IntArray get() = Arrays.copyOf(this._shape!!, this._shape!!.size)

    var flatSize = -1
    protected val isDynamic : Boolean

    abstract val dataType : DataType

    abstract val typeSize : Int

    abstract val floatArray : FloatArray
    abstract val intArray : IntArray

    abstract fun getIntValue(var1 : Int) : Int
    abstract fun getFloatValue(var1 : Int) : Float

    protected constructor(){
        this.isDynamic = true
        allocateMemory(intArrayOf(0))
    }

    protected constructor(shape : IntArray){
        this.isDynamic = false
        allocateMemory(shape)
    }

    fun loadBuffer(buffer : ByteBuffer, shape : IntArray){
        val flatSize = computeFlatSize(shape)
        require(buffer.limit() == (this.typeSize * flatSize)){"The size of byte buffer and the shape do not match."}

        if (!this.isDynamic) {
            require(flatSize == this.flatSize){"The size of byte buffer and the size of the tensor buffer do not match."}
        }else{
            this.flatSize = flatSize
        }

        _shape = shape.clone()
        buffer.rewind()
        this.buffer = buffer
    }

    fun loadBuffer(buffer : ByteBuffer){
        this.loadBuffer(buffer, _shape)
    }

    protected fun resize(shape : IntArray){
        if(this.isDynamic){
            this.allocateMemory(shape)
        }else{
            require(Arrays.equals(shape, this.shape))
            _shape = shape.clone()
        }
    }

    internal fun allocateMemory(shape : IntArray){
        require(isShapeValid(shape)){"Values in TensorBuffer shape should be non-negative."}
        val newFlatSize = computeFlatSize(shape)
        this._shape = shape.clone()
        if (flatSize != newFlatSize) {
            flatSize = newFlatSize
            buffer = ByteBuffer.allocateDirect(flatSize * this.typeSize)
            buffer!!.order(ByteOrder.nativeOrder())
        }
    }

    abstract fun loadArray(var1: IntArray, var2: IntArray)

    fun loadArray(src: IntArray) {
        this.loadArray(src, this._shape!!)
    }

    abstract fun loadArray(var1 : FloatArray, var2 : IntArray)

    fun loadArray(src : FloatArray){
        this.loadArray(src, this._shape!!)
    }

    companion object{
        fun createFixedSize(shape : IntArray, dataType : DataType) : NetBuffer {
            return when(dataType){
                DataType.FLOAT32-> NetBufferFloat(shape)
                DataType.INT32 -> NetBufferUint8(shape)
                else-> throw AssertionError("TensorBuffer does not support data type: " + dataType)
            }
        }

        fun createDynamic(dataType : DataType) : NetBuffer {
            return when(dataType){
                DataType.FLOAT32-> NetBufferFloat()
                DataType.INT32 -> NetBufferUint8()
                else-> throw AssertionError("TensorBuffer does not support data type: " + dataType)
            }
        }

        fun createFrom(buffer : NetBuffer?, dataType : DataType) : NetBuffer {
            buffer ?: throw NullPointerException("Buffer must not be null")

            val result : NetBuffer = if(buffer.isDynamic) createDynamic(dataType)!! else createFixedSize(buffer._shape!!, dataType)!!

            if(buffer.dataType == DataType.FLOAT32 && dataType == DataType.FLOAT32){
                val data = buffer.floatArray
                result.loadArray(data, buffer._shape!!)
            }else{
                val data = buffer.intArray
                result.loadArray(data, buffer._shape!!)
            }

            return result
        }

        fun computeFlatSize(shape : IntArray) : Int{
            var prod = 1
            val var3 = shape.size

            for (var4 in 0 until var3) {
                val s = shape[var4]
                prod *= s
            }

            return prod
        }

        fun isShapeValid(shape : IntArray) : Boolean{
            if(shape.size == 0) return true
            else{
                val var1 = shape
                val var2 = shape.size

                for(indx in 0 until var2){
                    var s= var1[indx]
                    if(s< 0)
                        return false
                }
                return true
            }
        }
    }

    private class NetBufferFloat : NetBuffer {
        constructor(shape : IntArray) : super(shape)
        constructor() : super()

        override val dataType: DataType = DATA_TYPE
        override val floatArray: FloatArray
            get() {
                this.buffer?.rewind()
                val arr = FloatArray(this.flatSize)
                val floatBuffer = this.buffer?.asFloatBuffer()
                floatBuffer?.get(arr)
                return arr
            }

        override fun getFloatValue(var1: Int) : Float {
            return buffer!!.getFloat(var1 shl 2)
        }

        override val intArray: IntArray
            get() {
                this.buffer?.rewind()
                val arr = IntArray(this.flatSize)
                val intBuffer = this.buffer?.asIntBuffer()
                intBuffer?.get(arr)
                return arr
            }

        override fun getIntValue(var1: Int) : Int {
            return buffer!!.getFloat(var1 shl 2).toInt()
        }

        override val typeSize: Int
            get() = DATA_TYPE.byteSize

        override fun loadArray(src: FloatArray, shape: IntArray) {
            require(src.size == computeFlatSize(shape)){"The size of the array to be loaded does not match the specified shape." }
            resize(shape)
            this.buffer?.rewind()
            val floatBuffer = this.buffer?.asFloatBuffer()
            floatBuffer?.put(src)
        }

        override fun loadArray(src: IntArray, shape: IntArray) {
            require(src.size == computeFlatSize(shape)){"The size of the array to be loaded does not match the specified shape." }
            this.resize(shape)
            this.buffer?.rewind()
            val intBuffer = this.buffer?.asIntBuffer()
            intBuffer?.put(src)
        }

        companion object{
            val DATA_TYPE = DataType.FLOAT32
        }
    }

    private class NetBufferUint8 : NetBuffer {
        constructor(shape : IntArray) : super(shape)
        constructor() : super()

        override val dataType: DataType = DATA_TYPE
        override val floatArray: FloatArray
            get() {
                buffer!!.rewind()
                val arr = FloatArray(flatSize)
                for (i in 0 until flatSize) {
                    arr[i] = (buffer!!.getInt() and 255) as Float
                }

                return arr
            }

        override fun getFloatValue(index: Int) : Float {
            return (buffer!!.getInt(index) and 255).toFloat()
        }

        override val intArray: IntArray
            get() {
                buffer!!.rewind()
                val arr = IntArray(flatSize)

                for (i in 0 until flatSize) {
                    arr[i] = buffer!!.getInt() and 255
                }
                return arr
            }

        override fun getIntValue(index: Int) : Int {
            return buffer!!.getInt(index) and 255
        }

        override val typeSize: Int = DATA_TYPE.byteSize


        override fun loadArray(src: FloatArray, shape: IntArray) {
            require(src.size == computeFlatSize(shape)){"The size of the array to be loaded does not match the specified shape." }
            resize(shape)
            buffer!!.rewind()
            val var4 = src.size

            for (var5 in 0 until var4) {
                val a = src[var5]
                buffer!!.put(
                    Math.max(Math.min(a.toDouble(), 255.0), 0.0).toInt().toByte()
                )
            }
            buffer!!.clear()
        }

        override fun loadArray(src: IntArray, shape: IntArray) {
            require(src.size == computeFlatSize(shape)){"The size of the array to be loaded does not match the specified shape." }

            resize(shape)
            buffer!!.rewind()
            val var4 = src.size

            for (var5 in 0 until var4) {
                val a = src[var5]
                buffer!!.put(Math.max(Math.min(a, 255), 0).toByte())
            }
        }

        companion object{
            val DATA_TYPE = DataType.UINT8
        }
    }
}
