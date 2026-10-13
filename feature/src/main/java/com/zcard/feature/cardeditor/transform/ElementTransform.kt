package com.zcard.feature.cardeditor.transform

import kotlin.math.abs

private const val POSITION_EPSILON = 1e-4f

data class ElementTransform(
    val posX: Float = 0f,
    val posY: Float = 0f,
    val posZ: Float = 0f,
    val rotation: Quaternion = Quaternion.identity,
    val scale: Int = 1
) {
    // float 연산 오차를 허용한 비교 — data class의 == 는 float를 정확히 비교하므로 미변경 판정에 부적합
    fun isApproximately(other: ElementTransform): Boolean =
        abs(posX - other.posX) < POSITION_EPSILON &&
        abs(posY - other.posY) < POSITION_EPSILON &&
        abs(posZ - other.posZ) < POSITION_EPSILON &&
        scale == other.scale &&
        rotation.isSameRotation(other.rotation)
}
