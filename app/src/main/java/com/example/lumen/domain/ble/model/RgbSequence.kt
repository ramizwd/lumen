package com.example.lumen.domain.ble.model

/**
 * Enum representing the color sequence of the LED strip.
 * The value corresponds to the byte received from the SP110E controller.
 */
enum class RgbSequence(
    val value: Byte,
) {
    RGB(0),
    RBG(1),
    GRB(2),
    GBR(3),
    BRG(4),
    BGR(5),
    ;

    companion object {
        fun fromByte(value: Byte): RgbSequence = entries.find { it.value == value } ?: RGB
    }
}
