package com.jiwon.lib_ai.model

class RuntimeConfig private constructor(private val builder : Builder) {
    enum class Delegate {
        CPU, GPU, XNNPACK, NNAPI, DALVIK
    }

    val numThread = builder.numThread
    val delegate = builder.delegate

    class Builder {
        var numThread = 4
        var delegate = Delegate.XNNPACK

        fun setNumThread(numThread : Int) : Builder {
            this.numThread = numThread
            return this
        }

        fun setDelegate(delegate : Delegate) : Builder {
            this.delegate = delegate
            return this
        }

        fun build() : RuntimeConfig {
            return RuntimeConfig(this)
        }
    }
}