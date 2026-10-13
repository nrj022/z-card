package com.zcard.feature.cardeditor.transform

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransformStateTest {

    private val initial = ElementTransform(
        posX = 0.3f,
        posY = -0.2f,
        posZ = 0.1f,
        rotation = Quaternion.euler(Rotation(y = 45f)),
        scale = 2
    )

    @Test
    fun `동일한 트랜스폼이면 변경 없음`() {
        val state = TransformState(initialTransform = initial, tempTransform = initial.copy())

        assertFalse(state.hasPendingTransform)
    }

    @Test
    fun `드래그로 회전했다가 되돌려 float 오차만 남으면 변경 없음`() {
        // 한 드래그 안에서 +30° 갔다가 -30° 돌아온 상황 — dragDelta가 정확히 0이 아님
        val start = initial.rotation
        val back = (Quaternion.euler(Rotation(y = 30f - 29.9999f)) * start).normalized()
        val state = TransformState(initialTransform = initial, tempTransform = initial.copy(rotation = back))

        assertFalse(state.hasPendingTransform)
    }

    @Test
    fun `위치를 이동했다가 되돌려 float 오차만 남으면 변경 없음`() {
        val moved = initial.posX + 0.1f + 0.1f + 0.1f - 0.1f - 0.1f - 0.1f
        val state = TransformState(initialTransform = initial, tempTransform = initial.copy(posX = moved))

        assertFalse(state.hasPendingTransform)
    }

    @Test
    fun `부호만 반대인 쿼터니언은 같은 회전이므로 변경 없음`() {
        val q = initial.rotation
        val state = TransformState(
            initialTransform = initial,
            tempTransform = initial.copy(rotation = Quaternion(-q.x, -q.y, -q.z, -q.w))
        )

        assertFalse(state.hasPendingTransform)
    }

    @Test
    fun `실제로 회전하면 변경 있음`() {
        val rotated = (Quaternion.euler(Rotation(x = 10f)) * initial.rotation).normalized()
        val state = TransformState(initialTransform = initial, tempTransform = initial.copy(rotation = rotated))

        assertTrue(state.hasPendingTransform)
    }

    @Test
    fun `위치나 스케일이 바뀌면 변경 있음`() {
        assertTrue(TransformState(initialTransform = initial, tempTransform = initial.copy(posY = initial.posY + 0.1f)).hasPendingTransform)
        assertTrue(TransformState(initialTransform = initial, tempTransform = initial.copy(scale = 3)).hasPendingTransform)
    }
}
