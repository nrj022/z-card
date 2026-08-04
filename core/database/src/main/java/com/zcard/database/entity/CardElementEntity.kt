package com.zcard.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_elements")
data class CardElementEntity(
    @PrimaryKey(autoGenerate = true)
    val elementId: Long = 0,

    val cardId: Long,              // FK to Card.cardId
    val assetId: Long? = null,     // null when elementType = TEXT
    val elementType: String, // ElementType: OBJECT / TEXT

    // Transform
    val posX: Float,
    val posY: Float,
    val posZ: Float = 0f,

    // Quaternion (identity: 0,0,0,1) — Android 좌표계 기준
    val quatX: Float = 0f,
    val quatY: Float = 0f,
    val quatZ: Float = 0f,
    val quatW: Float = 1f,

    val scale: Int = 1,

    // Text fields (nullable)
    val textContent: String? = null,
    val fontFamily: String? = null,
    val fontSize: Float? = null,
    val textColor: String? = null,
    val textAlign: String? = null,   // UnityTextAlign: LEFT / CENTER / RIGHT
)