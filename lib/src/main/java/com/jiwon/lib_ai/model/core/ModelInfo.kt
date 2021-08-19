package com.jiwon.lib_ai.model.core

import com.jiwon.lib_ai.model.DataType

class ModelInfo(private val builder : Builder) {
    internal val modelName : String = builder.modelName

    internal var inputShape : Array<IntArray>? = builder.inputShape
    internal var outputShape : Array<IntArray>? = builder.outputShape
    internal var dataType : DataType? = builder.dataType

    class Builder(internal val modelName : String) {
        internal var inputShape : Array<IntArray>? = null
        internal var outputShape : Array<IntArray>? = null
        internal var dataType : DataType? = null

        /**
         * Sets input shape if required
         *
         * @param shape of the tensor [n, w, h, c]
         * @return ModelInfoBuilder
         */
        fun setInputShape(shape : Array<IntArray>) : Builder{
            inputShape = shape
            return this
        }


        /**
         * Sets input shape if required
         *
         * @param shape of the tensor [n, w, h, c]
         * @return ModelInfoBuilder
         */
        fun setOutputShape(shape : Array<IntArray>) : Builder{
            this.outputShape = shape
            return this
        }

        fun setDataType(dataType : DataType) : Builder{
            this.dataType = dataType
            return this
        }

        fun build() : ModelInfo{
            return ModelInfo(this)
        }
    }
}