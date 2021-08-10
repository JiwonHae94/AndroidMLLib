package com.jiwon.lib_ai.face.detector

import android.graphics.Bitmap
import com.jiwon.lib_ai.model.tflite.TfliteModel
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage

class BlazeFace : TfliteModel<String>, FaceDetectorImple{

    override fun detect(var1: Bitmap) {
        run(var1)
    }

    override fun preprocessInput(vararg var1: Any): TensorImage {
        TODO("Not yet implemented")
    }

    override fun processOutput() {
        TODO("Not yet implemented")
    }
}