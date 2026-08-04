package com.zcard.domain.model

import kotlin.random.Random

data class CardElement(
    val elementId: Long = 0,

    val cardId: Long,
    val assetId: Long? = null,
    val elementType: ElementType,

    // Transform
    val posX: Float = Random.nextInt(-20, 21) / 10f,
    val posY: Float = Random.nextInt(-20, 21) / 10f,
    val posZ: Float = Random.nextInt(-10, 11) / 10f,

    // Quaternion (identity: 0,0,0,1) — Android 좌표계 기준
    val quatX: Float = 0f,
    val quatY: Float = 0f,
    val quatZ: Float = 0f,
    val quatW: Float = 1f,

    val scale: Int = 1,

    // Text fields (nullable)
    val textAttributes: TextAttributes? = null
)