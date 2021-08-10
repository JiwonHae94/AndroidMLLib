package com.jiwon.lib_ai.model.exe

class ExecutionOption private constructor(private val builder : Builder) {
    enum class DELEGATE {
        CPU, GPU, XNNPACK, NNAPI, DALVIK
    }

    internal val numThread : Int = builder.numThread
    internal val delegate : DELEGATE = builder.delegate

    internal class Builder {
        internal var numThread = 4
        internal var delegate = DELEGATE.GPU

        fun setNumThread(var1 : Int) : Builder{
            this.numThread = var1
            return this
        }

        fun setDelegate(var1 : DELEGATE) : Builder {
            this.delegate = var1
            return this
        }

        fun build() : ExecutionOption {
            return ExecutionOption(this)
        }
    }
}