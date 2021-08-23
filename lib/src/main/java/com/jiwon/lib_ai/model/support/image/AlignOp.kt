package com.jiwon.lib_ai.model.support.image

import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.lang.NullPointerException
import java.nio.DoubleBuffer

class AlignOp (val landmarks : DoubleBuffer) : ImageOperator() {
    override fun apply(var1: Mat): Mat {
        if(landmarks.array().isEmpty())
            throw NullPointerException("not sufficient landmark is provided")

        val left_eye = doubleArrayOf(landmarks.get(0), landmarks.get(1))
        val right_eye = doubleArrayOf(landmarks.get(2), landmarks.get(3))
        val mouse_left = doubleArrayOf(landmarks.get(6), landmarks.get(7))
        val mouse_right = doubleArrayOf(landmarks.get(8), landmarks.get(9))
        val middle_mouse = doubleArrayOf(
            (mouse_left[0] + mouse_right[0]) / 2,
            (mouse_left[1] + mouse_right[1]) / 2
        )

        // float margin[] = {0.5f, 0.57f, 0.57f};
        val margin = floatArrayOf(1.08f, 1.27f, 0.47f)

        // Get Width, Height
        val width =
            Math.sqrt((right_eye[0] - left_eye[0]) * (right_eye[0] - left_eye[0]) + ((right_eye[1] - left_eye[1]) * (right_eye[1] - left_eye[1])))
                .toInt()
        val height = (Math.abs(
            (right_eye[1] - left_eye[1]) * middle_mouse[0] - (right_eye[0] - left_eye[0]) * middle_mouse[1] + right_eye[0] * left_eye[1] - right_eye[1] * left_eye[0]
        )
                / Math.pow(
            Math.pow(
                right_eye[1] - left_eye[1].toDouble(), 2.0
            ) + Math.pow(right_eye[0] - left_eye[0].toDouble(), 2.0), 0.5
        )).toInt()

        // Get Margin
        margin[0] = width * margin[0]
        margin[1] = height * margin[1]
        margin[2] = height * (1 + margin[2])

        // Get Base Line
        val atan_top =
            Math.atan((right_eye[1] - left_eye[1]) / (right_eye[0] - left_eye[0]).toDouble())
        val x_delta = Math.cos(atan_top)
        val y_delta = -Math.sin(atan_top)

        val base1 = doubleArrayOf(
            left_eye[0] - x_delta * margin[0],
            left_eye[1] + y_delta * margin[0]
        )

        val base2 = doubleArrayOf(
            right_eye[0] + x_delta * margin[0],
            right_eye[1] - y_delta * margin[0]
        )

        // Get Bounding Box
        val top_left = doubleArrayOf(
            base1[0] - y_delta * margin[1],
            base1[1] - x_delta * margin[1]
        )
        val top_right = doubleArrayOf(
            base2[0] - y_delta * margin[1],
            base2[1] - x_delta * margin[1]
        )
        val bottom_left = doubleArrayOf(
            base1[0] + y_delta * margin[2],
            base1[1] + x_delta * margin[2]
        )
        val bottom_right = doubleArrayOf(
            base2[0] + y_delta * margin[2],
            base2[1] + x_delta * margin[2]
        )

        // Get Max Width, Height
        val widthA = Math.sqrt(
            Math.pow(
                bottom_right[0] - bottom_left[0],
                2.0
            ) + Math.pow(bottom_right[1] - bottom_left[1], 2.0)
        )
        val widthB = Math.sqrt(
            Math.pow(
                top_right[0] - top_left[0],
                2.0
            ) + Math.pow(top_right[1] - top_left[1], 2.0)
        )
        val maxWidth = Math.max(widthA, widthB)
        val heightA = Math.sqrt(
            Math.pow(
                top_right[0] - bottom_right[0],
                2.0
            ) + Math.pow(top_right[1] - bottom_right[1], 2.0)
        )
        val heightB = Math.sqrt(
            Math.pow(
                top_left[0] - bottom_left[0],
                2.0
            ) + Math.pow(top_left[1] - bottom_left[1], 2.0)
        )
        val maxHeight = Math.max(heightA, heightB)

        // Prepare MAT for CV
        val srcPoints = arrayListOf(
            Point(top_left[0], top_left[1]),
            Point(top_right[0], top_right[1]),
            Point(bottom_right[0], bottom_right[1]),
            Point(bottom_left[0], bottom_left[1])
        )

        val dstPoints = arrayListOf(
            Point(0.0, 0.0),
            Point(maxWidth - 1, 0.0),
            Point(maxWidth - 1, maxHeight - 1),
            Point(0.0, maxHeight - 1)
        )

        // Transform and resize
        val matPt_src = MatOfPoint2f()
        val matPt_dst = MatOfPoint2f()
        matPt_src.fromList(srcPoints)
        matPt_dst.fromList(dstPoints)

        val tfm = Imgproc.getPerspectiveTransform(matPt_src, matPt_dst)
        Imgproc.warpPerspective(var1, var1, tfm, Size(maxWidth, maxHeight))

        return var1
    }
}