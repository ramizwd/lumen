package com.example.lumen.domain.ble.model

/**
 * Enum representing the IC model of the LED strip.
 * The value corresponds to the byte sent to/received from the SP110E controller.
 */
enum class IcModel(
    val value: Byte,
) {
    SM16703(0x00),
    TM1804(0x01),
    UCS1903(0x02),
    WS2811(0x03),
    WS2801(0x04),
    SK6812(0x05),
    LPD6803(0x06),
    LPD8806(0x07),
    APA102(0x08),
    APA105(0x09),
    DMX512(0x0A),
    TM1914(0x0B),
    TM1913(0x0C),
    P9813(0x0D),
    INK1003(0x0E),
    P943S(0x0F),
    P9411(0x10),
    P9413(0x11),
    TX1812(0x12),
    TX1813(0x13),
    GS8206(0x14),
    GS8208(0x15),
    SK9822(0x16),
    TM1814(0x17),
    SK6812_RGBW(0x18),
    P9414(0x19),
    PG412(0x1A),
    ;

    companion object {
        fun fromByte(value: Byte): IcModel = entries.find { it.value == value } ?: WS2811
    }
}
