package com.jiwon.lib_ai.model

class ImageProcessor private constructor(val builder : Builder) {

    class Builder{

        fun add() : Builder{
            return this
        }

        fun build() : ImageProcessor{
            return ImageProcessor(this)
        }
    }
}