package com.example.lumen.data.mapper

import com.example.lumen.domain.ble.model.IcModel
import com.example.lumen.domain.ble.model.LedControllerState
import com.example.lumen.domain.ble.model.RgbSequence

/**
 * Parses the 12 byte response from the LED controller that holds the device's state
 */
fun ByteArray.toLedControllerState(): LedControllerState {
    if (this.size < 12) {
        throw IllegalArgumentException("Invalid data length! Expected 12 bytes.")
    }

    val isOn = this[0] == 0x01.toByte()
    val preset = this[1]
    val speed = this[2]
    val brightness = this[3].toUByte().toFloat()
    val icModel = IcModel.fromByte(this[4])
    val rgbSeq = RgbSequence.fromByte(this[5])
    val pixelCountMSB = this[6].toUByte().toInt()
    val pixelCountLSB = this[7].toUByte().toInt()
    val totalActivePixels = (pixelCountMSB shl 8) or pixelCountLSB
    val red = this[8].toHexString()
    val green = this[9].toHexString()
    val blue = this[10].toHexString()
    val whiteLedBrightness = this[11]

    return LedControllerState(
        isOn = isOn,
        preset = preset,
        speed = speed,
        brightness = brightness,
        icModel = icModel,
        rgbSeq = rgbSeq,
        totalActivePixels = totalActivePixels,
        red = red,
        green = green,
        blue = blue,
        whiteLedBrightness = whiteLedBrightness,
    )
}
