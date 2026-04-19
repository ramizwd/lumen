package com.example.lumen.utils

import com.example.lumen.domain.ble.model.GattConstants.BRIGHTNESS_SUFFIX_HEX
import com.example.lumen.domain.ble.model.GattConstants.COLOR_SUFFIX_HEX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MAX
import com.example.lumen.utils.AppConstants.BRIGHTNESS_MIN
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

/**
 * Unit tests for conversion extension functions
 */
class ConversionsTest {

    @Test
    fun `toBrightnessCommandBytes value above max coerces to byte array`() {
        // Given
        val value = 1000f
        val expectedBytes = "${String.format("%02X", BRIGHTNESS_MAX.toInt())}${BRIGHTNESS_SUFFIX_HEX}"
            .hexToByteArray()

        // When
        val result = value.toBrightnessCommandBytes()

        // Then
        assertArrayEquals(expectedBytes, result)
    }

    @Test
    fun `toBrightnessCommandBytes value below min coerces to byte array`() {
        val value = -1f
        val expectedBytes = "${String.format("%02X", BRIGHTNESS_MIN.toInt())}${BRIGHTNESS_SUFFIX_HEX}"
            .hexToByteArray()

        val result = value.toBrightnessCommandBytes()

        assertArrayEquals(expectedBytes, result)
    }

    @Test
    fun `hexToColorCommandBytes valid hex returns suffixed byte array`() {
        val value = "ffffff"
        val expectedBytes = (value + COLOR_SUFFIX_HEX).hexToByteArray()

        val result = value.hexToColorCommandBytes()

        assertArrayEquals(expectedBytes, result)
    }
}