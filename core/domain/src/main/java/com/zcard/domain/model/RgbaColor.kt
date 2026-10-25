package com.zcard.domain.model

data class RgbaColor(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float
) {
    companion object {
        fun fromArgb(argb: Int): RgbaColor {
            val a = (argb shr 24 and 0xFF) / 255.0f
            val r = (argb shr 16 and 0xFF) / 255.0f
            val g = (argb shr 8 and 0xFF) / 255.0f
            val b = (argb and 0xFF) / 255.0f
            return RgbaColor(r, g, b, a)
        }
    }
}