package com.jiwon.lib_ai.model

enum class DataType(val value : Int) {
    FLOAT32(1),
    INT32(2),
    UINT8(3),
    INT64(4),
    STRING(5),
    INT8(9);

    val byteSize : Int
        get(){
            return when(this){
                FLOAT32 -> 4
                INT32  -> 4
                INT8, UINT8 -> 1
                INT64 -> 8
                STRING -> -1
                else -> throw IllegalArgumentException("DataType error: DataType " + this + " is not supported yet");
            }
        }
}