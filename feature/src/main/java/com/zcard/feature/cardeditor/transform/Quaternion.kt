package com.zcard.feature.cardeditor.transform

import kotlin.math.*

data class Quaternion(val x: Float, val y: Float, val z: Float, val w: Float) {

    // Quaternion 곱셈 (Hamilton product)
    operator fun times(q: Quaternion) = Quaternion(
        w*q.x + x*q.w + y*q.z - z*q.y,
        w*q.y - x*q.z + y*q.w + z*q.x,
        w*q.z + x*q.y - y*q.x + z*q.w,
        w*q.w - x*q.x - y*q.y - z*q.z
    )

    // 단위 쿼터니언으로 정규화 — 반복 곱셈의 float 오차 누적 방지
    // q와 -q는 같은 회전이므로 w ≥ 0으로 부호를 통일한다
    fun normalized(): Quaternion {
        val n = sqrt(x*x + y*y + z*z + w*w)
        if (n < 1e-6f) return identity  // (0,0,0,0) 등 유효하지 않은 값 방어
        val s = if (w < 0) -1f / n else 1f / n
        return Quaternion(x*s, y*s, z*s, w*s)
    }

    // 같은 회전인지 오차 허용 비교 (q와 -q 동일 취급). eps 1e-5 ≈ 0.5°
    fun isSameRotation(other: Quaternion, eps: Float = 1e-5f): Boolean =
        abs(x*other.x + y*other.y + z*other.z + w*other.w) > 1f - eps

    // Unity YXZ 역변환 (Quaternion → Euler degrees)
    fun toEuler(): Rotation {
        val qx = x.toDouble(); val qy = y.toDouble()
        val qz = z.toDouble(); val qw = w.toDouble()

        val sinX = 2.0 * (qw * qx - qy * qz)
        return if (abs(sinX) >= 0.9999) {
            // 짐벌락: X축이 ±90°일 때 Y, Z를 분리할 수 없어 Z=0으로 고정
            Rotation(
                x = (90.0 * sign(sinX)).toFloat(),
                y = Math.toDegrees(atan2(2.0 * (qy * qw - qx * qz), 1.0 - 2.0 * (qy * qy + qz * qz))).toFloat(),
                z = 0f
            )
        } else {
            Rotation(
                x = Math.toDegrees(asin(sinX)).toFloat(),
                y = Math.toDegrees(atan2(2.0 * (qx * qz + qw * qy), 1.0 - 2.0 * (qx * qx + qy * qy))).toFloat(),
                z = Math.toDegrees(atan2(2.0 * (qx * qy + qw * qz), 1.0 - 2.0 * (qx * qx + qz * qz))).toFloat()
            )
        }
    }

    companion object {
        val identity = Quaternion(0f, 0f, 0f, 1f)

        // Unity의 Quaternion.Euler(x, y, z) — 내부 순서: Z → X → Y
        fun euler(rotation: Rotation): Quaternion {
            val xr = Math.toRadians(rotation.x.toDouble())
            val yr = Math.toRadians(rotation.y.toDouble())
            val zr = Math.toRadians(rotation.z.toDouble())
            val cx = cos(xr / 2).toFloat(); val sx = sin(xr / 2).toFloat()
            val cy = cos(yr / 2).toFloat(); val sy = sin(yr / 2).toFloat()
            val cz = cos(zr / 2).toFloat(); val sz = sin(zr / 2).toFloat()
            return Quaternion(
                cx*sy*sz + sx*cy*cz,
                cx*sy*cz - sx*cy*sz,
                cx*cy*sz - sx*sy*cz,
                cx*cy*cz + sx*sy*sz
            )
        }
    }
}
