package com.example.lumen.domain.ble.model

/**
 * enum for all predefined colors the user can choose from.
 */
enum class PresetLedColors(val hex: String) {
    RED("ff0000"),
    GREEN("00ff00"),
    BLUE("0000ff"),
    YELLOW("ffff00"),
    PURPLE("ff00ff"),
    CYAN("00ffff"),
    WHITE("ffffff");
}