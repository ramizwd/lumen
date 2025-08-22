package com.example.lumen.domain.ble.model

/**
 * enum for all preset colors the user can choose from.
 * Format: RR GG BB and 1E (command byte)
 */
enum class StaticLedColors(val hex: String) {
    RED("ff00001e"),
    GREEN("00ff001e"),
    BLUE("0000ff1e"),
    YELLOW("ffff001e"),
    PURPLE("ff00ff1e"),
    CYAN("00ffff1e"),
    WHITE("ffffff1e");

    val commandBytes: ByteArray by lazy { hex.hexToByteArray() }
}