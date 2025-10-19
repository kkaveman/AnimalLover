package com.example.animallover

import android.graphics.Color

object Constants {
    const val MODEL_PATH = "model.tflite"
    const val LABELS_PATH = "labels.txt"

    val BOUNDING_BOX_COLORS = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.CYAN,
        Color.MAGENTA,
        Color.YELLOW,
        Color.WHITE,
        Color.BLACK
    )
}