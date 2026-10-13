package com.zcard.feature.cardeditor.transform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

private const val DELTA = 0.01f

/** 두 쿼터니언이 같은 회전을 나타내는지 확인 (q와 -q는 같은 회전). */
private fun assertSameRotation(expected: Quaternion, actual: Quaternion, delta: Float = DELTA) {
    val sameSign = abs(expected.x - actual.x) < delta &&
        abs(expected.y - actual.y) < delta &&
        abs(expected.z - actual.z) < delta &&
        abs(expected.w - actual.w) < delta
    val flippedSign = abs(expected.x + actual.x) < delta &&
        abs(expected.y + actual.y) < delta &&
        abs(expected.z + actual.z) < delta &&
        abs(expected.w + actual.w) < delta

    assertTrue(
        "Expected $expected to represent the same rotation as $actual",
        sameSign || flippedSign
    )
}

class QuaternionTest {

    @Test
    fun `identity euler is zero`() {
        val euler = Quaternion.identity.toEuler()

        assertEquals(0f, euler.x, DELTA)
        assertEquals(0f, euler.y, DELTA)
        assertEquals(0f, euler.z, DELTA)
    }

    @Test
    fun `euler round trip on single axis X`() {
        val original = Rotation(x = 30f, y = 0f, z = 0f)

        val result = Quaternion.euler(original).toEuler()

        assertEquals(original.x, result.x, DELTA)
        assertEquals(original.y, result.y, DELTA)
        assertEquals(original.z, result.z, DELTA)
    }

    @Test
    fun `euler round trip on single axis Y`() {
        val original = Rotation(x = 0f, y = 45f, z = 0f)

        val result = Quaternion.euler(original).toEuler()

        assertEquals(original.x, result.x, DELTA)
        assertEquals(original.y, result.y, DELTA)
        assertEquals(original.z, result.z, DELTA)
    }

    @Test
    fun `euler round trip on single axis Z`() {
        val original = Rotation(x = 0f, y = 0f, z = 60f)

        val result = Quaternion.euler(original).toEuler()

        assertEquals(original.x, result.x, DELTA)
        assertEquals(original.y, result.y, DELTA)
        assertEquals(original.z, result.z, DELTA)
    }

    @Test
    fun `euler round trip on combined axes away from gimbal lock`() {
        val original = Rotation(x = 15f, y = -25f, z = 40f)

        val result = Quaternion.euler(original).toEuler()

        assertEquals(original.x, result.x, DELTA)
        assertEquals(original.y, result.y, DELTA)
        assertEquals(original.z, result.z, DELTA)
    }

    @Test
    fun `gimbal lock at X 90 forces Z to zero but preserves the actual rotation`() {
        val original = Rotation(x = 90f, y = 40f, z = 25f)
        val quaternion = Quaternion.euler(original)

        val decomposed = quaternion.toEuler()
        assertEquals(90f, decomposed.x, DELTA)
        assertEquals(0f, decomposed.z, DELTA)

        // Z 정보는 개별적으로 복원 불가하지만, 분해된 Euler를 다시 합성하면
        // 같은 회전(쿼터니언)으로 돌아와야 한다 (짐벌락이어도 정보 손실 없이 표현 가능함을 보장).
        val recomposed = Quaternion.euler(decomposed)
        assertSameRotation(quaternion, recomposed)
    }

    @Test
    fun `times with identity returns the same quaternion`() {
        val q = Quaternion.euler(Rotation(x = 20f, y = 30f, z = 40f))

        assertSameRotation(q, q.times(Quaternion.identity))
        assertSameRotation(q, Quaternion.identity.times(q))
    }

    @Test
    fun `times is not commutative for different axes`() {
        val rotateX90 = Quaternion.euler(Rotation(x = 90f))
        val rotateY90 = Quaternion.euler(Rotation(y = 90f))

        val xThenY = rotateY90.times(rotateX90)
        val yThenX = rotateX90.times(rotateY90)

        val different = abs(xThenY.x - yThenX.x) > DELTA ||
            abs(xThenY.y - yThenX.y) > DELTA ||
            abs(xThenY.z - yThenX.z) > DELTA ||
            abs(xThenY.w - yThenX.w) > DELTA

        assertTrue("Rotation composition order should matter (X then Y != Y then X)", different)
    }

    @Test
    fun `normalized returns unit length with non-negative w`() {
        val q = Quaternion(0.6f, -1.2f, 0.4f, -2f).normalized()
        val norm = kotlin.math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w)

        assertEquals(1f, norm, 1e-5f)
        assertTrue("w should be non-negative", q.w >= 0f)
    }

    @Test
    fun `normalized falls back to identity for zero quaternion`() {
        assertEquals(Quaternion.identity, Quaternion(0f, 0f, 0f, 0f).normalized())
    }

    @Test
    fun `repeated small rotations stay unit length when normalized`() {
        val step = Quaternion.euler(Rotation(x = 0.37f, y = 0.41f, z = 0.29f))
        var q = Quaternion.identity
        repeat(10_000) { q = (step * q).normalized() }

        val norm = kotlin.math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w)
        assertEquals(1f, norm, 1e-5f)
    }

    @Test
    fun `isSameRotation treats q and negative q as equal`() {
        val q = Quaternion.euler(Rotation(x = 20f, y = 30f, z = 40f))
        val negated = Quaternion(-q.x, -q.y, -q.z, -q.w)

        assertTrue(q.isSameRotation(negated))
    }

    @Test
    fun `isSameRotation tolerates tiny drift but rejects visible difference`() {
        val q = Quaternion.euler(Rotation(y = 30f))

        assertTrue(q.isSameRotation(Quaternion.euler(Rotation(y = 30.1f))))
        assertTrue(!q.isSameRotation(Quaternion.euler(Rotation(y = 32f))))
    }
}
