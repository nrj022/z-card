package com.zcard.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// rotX/rotY/rotZ(Euler Int) → quatX/quatY/quatZ/quatW(Quaternion Float) 전환.
// 기존 rotX/Y/Z 값은 실제로 저장 경로가 없어 항상 기본값이었으므로 identity(0,0,0,1)로 리셋해도 손실 없음.
// SQLite RENAME COLUMN은 3.25+ 필요(minSdk 26 기기의 번들 SQLite로는 보장 안 됨)라 재생성 방식 사용.
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS card_elements_new")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS card_elements_new (
                elementId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                cardId INTEGER NOT NULL,
                assetId INTEGER,
                elementType TEXT NOT NULL,
                posX REAL NOT NULL,
                posY REAL NOT NULL,
                posZ REAL NOT NULL DEFAULT 0,
                quatX REAL NOT NULL DEFAULT 0,
                quatY REAL NOT NULL DEFAULT 0,
                quatZ REAL NOT NULL DEFAULT 0,
                quatW REAL NOT NULL DEFAULT 1,
                scale INTEGER NOT NULL DEFAULT 1,
                textContent TEXT,
                fontFamily TEXT,
                fontSize REAL,
                textColor TEXT,
                textAlign TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO card_elements_new (
                elementId, cardId, assetId, elementType, posX, posY, posZ,
                quatX, quatY, quatZ, quatW, scale, textContent, fontFamily, fontSize, textColor, textAlign
            )
            SELECT
                elementId, cardId, assetId, elementType, posX, posY, posZ,
                0, 0, 0, 1, scale, textContent, fontFamily, fontSize, textColor, textAlign
            FROM card_elements
            """.trimIndent()
        )
        db.execSQL("DROP TABLE card_elements")
        db.execSQL("ALTER TABLE card_elements_new RENAME TO card_elements")
    }
}
