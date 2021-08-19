package com.jiwon.lib_ai

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Utility {

    @Throws(IOException::class)
    fun getAssetAddress(context:Context = LibAI.getApplicationContext(), fileName : String) : String{
        val file = File(context.filesDir, fileName)

        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }

        context.assets.open(fileName).use { inputStream ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.getAbsolutePath()
        }
    }
}