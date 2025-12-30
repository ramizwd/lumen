# Lumen - SP110E LED controller app
## About
Lumen is built as an alternative open source app for controlling the SP110E Bluetooth LED device from Android devices. It aims to provide a more robust and nicer experience compared to the official app. It is developed following Android development best practices and design principles with modern Material Design guidelines in mind.

This app is developed partly because of how clumsy and lackluster in features the official app is, so I decided to reverse the communication protocol between the app and the controller, building this to utilize the controller's full potential and to include additional functionality for increased versatility. This project also served as a learning experience in the basics of reverse engineering and Bluetooth sniffing.

### Insight
Here's a quick rundown of how I managed to receive and send info to the SP110E device:

The controller is reversed to expose its services, their characteristics, and the commands required for control by using Wireshark and nRF Connect.

The device has three services. The key service (`0xFFE0`) contains a characteristic (`0xFFE1`) that supports both write and notify. That characteristic is also the one with a descriptor (`0x2902`). Hence, it was clear this is the one to write operations to for controlling the device and receiving information from.

For receiving the controller's state, once the device is connected, notifications were enabled for that writable characteristic's CCCD. Once notifications were enabled, then requesting controller's state by writing `0x10` command byte to the characteristic `0xFFE1`. Once the command is sent, then the device transmits a notification containing 12 bytes of data representing its current state.

Figuring out the commands for controlling the device was done by pairing the official app with the controller and using it to capture the Bluetooth communication between them, then reading the payload sent to the device using Wireshark. For example, turning the device *on* is done by sending `0xAA` command byte, or `0xAB` for turning it *off*. Changing the color is done by sending data in the format of RR GG BB (0-255) with `1E` as the command byte, so for red it will be `FF 00 00 1E`.

You can find a comprehensive list of commands [here](https://gist.github.com/mbullington/37957501a07ad065b67d4e8d39bfe012).

## Compatibility
- **Minimum SDK:** 31 (Android 12)
- **Target SDK:** 36 (Android 16)
- **Device Types Supported:** Phones and Tablets
- **Orientation Supported**: Portrait and Landscape

## Features
The following highlights both core functionalities and new or improved features compared to the official app. 

**Core Functionality:**
- Scan for nearby Bluetooth devices
- Connect to SP110E controllers
- Set LED color using the preset colors or color picker
- Save custom colors
- Change brightness
- Turn the LED on/off
- Rename the controller

**Lumen-specific Improvements & New Features:**
- Offers vastly greater number of color combinations by using an HSV color picker instead of a
  limited hue picker
- Improved UI and UX designed using [Material 3 guidelines](https://m3.material.io/), with dynamic themes and dark mode support
- Favor devices for quicker access
- Overall better error and permission handling
- Automatic reconnection when an ongoing connection attempt drops
- Adaptive layout supporting different screen sizes and configs
- Cancel an ongoing connection attempt
- Match the LED colors based on your current color theme
- Pick random colors

> [!NOTE]
> The app is still in development, thus the following features that exist in the official app
> are currently missing here.
> - Choosing from the available 120 light effects and favoring them
> - Controlling specific amount of pixels on the LED strip
> - Setting RGB sequence and IC type

## Dependencies
This project utilizes the following third-party libraries.

- **Compose Material 3 Adaptive**
  - https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive
  - Library for creating adaptive UIs
 
- **Color Picker Compose**
  - https://github.com/skydoves/colorpicker-compose
  - A color picker library for Jetpack Compose

- **Dagger-Hilt**
  - https://github.com/google/dagger
  - Dependency injection framework for Android
 
- **JUnit**
  - https://github.com/junit-team/junit-framework
  - Unit testing framework for Java and the JVM

- **Kotlinx Coroutines**
  - https://github.com/Kotlin/kotlinx.coroutines
  - Kotlin library for asynchronous code

- **Kotlinx Serialization JSON**
  - https://github.com/Kotlin/kotlinx.serialization
  - A multiplatform serialization library for Kotlin
  
- **MockK**
  - https://github.com/mockk/mockk
  - Mocking library for Kotlin

- **Navigation Compose**
  - https://developer.android.com/jetpack/androidx/releases/navigation
  - Navigation library for Jetpack Compose

- **Preferences DataStore**
  - https://developer.android.com/jetpack/androidx/releases/datastore
  - Jetpack DataStore for storing key-value paris

- **Timber**
  - https://github.com/JakeWharton/timber
  - An extensible logging library for Android

- **Turbine**
  - https://github.com/cashapp/turbine
  - A testing library for kotlinx.coroutines Flow

## Icons
Icons used in this project are from [Material Symbols and Icons](https://fonts.google.com/icons)
- https://github.com/google/material-design-icons

## Material
- [Extensive SP110E API documentation](https://gist.github.com/mbullington/37957501a07ad065b67d4e8d39bfe012)
- [Reverse Engineering a Bluetooth Light Bulb](https://blog.wokwi.com/reverse-engineering-a-bluetooth-lightbulb)

## License
This project is licensed under the [GPLv3 license](LICENSE)
