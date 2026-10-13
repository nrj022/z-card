package com.zcard.data.mapper

import com.zcard.domain.model.CardElement
import com.zcard.domain.model.ElementType
import org.junit.Assert.assertEquals
import org.junit.Test

class UnityMapperTest {

    @Test
    fun `toUpdateRotationDto는 identity를 identity로 유지한다`() {
        val dto = UnityMapper.toUpdateRotationDto(elementId = 1L, quatX = 0f, quatY = 0f, quatZ = 0f, quatW = 1f)

        assertEquals(0f, dto.value.x, 0.0001f)
        assertEquals(0f, dto.value.y, 0.0001f)
        assertEquals(0f, dto.value.z, 0.0001f)
        assertEquals(1f, dto.value.w, 0.0001f)
    }

    @Test
    fun `toUpdateRotationDto은 Unity 좌표계 쿼터니언을 변환 없이 전달한다`() {
        val dto = UnityMapper.toUpdateRotationDto(elementId = 1L, quatX = 0.3f, quatY = -0.4f, quatZ = 0.09f, quatW = 0.85f)

        assertEquals(0.3f, dto.value.x, 0.0001f)
        assertEquals(-0.4f, dto.value.y, 0.0001f)
        assertEquals(0.09f, dto.value.z, 0.0001f)
        assertEquals(0.85f, dto.value.w, 0.0001f)
    }

    @Test
    fun `toObjectDto 은 toUpdateRotationDto과 동일한 변환이 수행된다`() {
        val element = CardElement(
            cardId = 1L,
            elementType = ElementType.OBJECT,
            quatX = 0.3f,
            quatY = -0.4f,
            quatZ = 0.09f,
            quatW = 0.85f,
        )

        val objectDto = UnityMapper.toObjectDto(unityKey = "prefab", elementId = 1L, element = element)
        val updateDto = UnityMapper.toUpdateRotationDto(elementId = 1L, quatX = element.quatX, quatY = element.quatY, quatZ = element.quatZ, quatW = element.quatW)

        assertEquals(updateDto.value, objectDto.rotation)
    }
}
