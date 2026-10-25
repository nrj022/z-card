package com.zcard.domain.model

data class Asset(
    val assetId: Long = 0,
    val assetType: AssetType,
    val unityKey: String,
    val thumbnailKey: String
) {
    companion object {
        private const val SOLID_BACKGROUND_ASSET_ID = 27L

        fun isSolidBackground(assetId: Long) = assetId == SOLID_BACKGROUND_ASSET_ID
    }
}
