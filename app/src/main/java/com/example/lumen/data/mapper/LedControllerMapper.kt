package com.example.lumen.data.mapper

import com.example.lumen.domain.ble.model.LedControllerInfo

/**
 * Parses the 12 byte response from the LED controller that holds the device's state
 */
fun ByteArray.toLedControllerInfo(): LedControllerInfo {
    if (this.size < 12) {
        throw IllegalArgumentException("Invalid data length! Expected 12 bytes.")
    }

    val isOn = this[0] == 0x01.toByte()
    val preset = this[1]
    val speed = this[2]
    val brightness = this[3]
    val icMode = this[4]
    val channel = this[5]
    val pixelCountMSB = this[6].toUByte().toInt()
    val pixelCountLSB = this[7].toUByte().toInt()
    val pixelCount = (pixelCountMSB shl 8) or pixelCountLSB
    val red = this[8].toHexString()
    val green = this[9].toHexString()
    val blue = this[10].toHexString()
    val whiteLedBrightness = this[11]

    return LedControllerInfo(
        isOn = isOn,
        preset = preset,
        speed = speed,
        brightness = brightness,
        icModel = icMode,
        channel = channel,
        pixelCount = pixelCount,
        red = red,
        green = green,
        blue = blue,
        whiteLedBrightness = whiteLedBrightness
    )
}
