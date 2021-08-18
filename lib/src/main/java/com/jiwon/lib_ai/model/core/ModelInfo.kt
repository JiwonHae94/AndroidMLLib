package com.jiwon.lib_ai.model.core

class ModelInfo(private val builder : Builder) {
    internal val modelName : String = builder.modelName
    internal var inputShape : IntArray? = builder.inputShape

    class Builder(internal val modelName : String) {
        internal var inputShape : IntArray? = null
    }
}