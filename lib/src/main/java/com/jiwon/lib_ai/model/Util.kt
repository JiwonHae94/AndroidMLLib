package com.jiwon.lib_ai.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object Util {
    fun Int.toFloatBuffer() : FloatBuffer {
        return ByteBuffer.allocate(this)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }
}