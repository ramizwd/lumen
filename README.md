# Lumen - SP110E LED controller app
## About
Lumen is built as an alternative app for controlling the SP110E LED device from Android devices.
It aims to provide a more robust and feature-rich experience compared to the official app. It is
developed following Android development best practices and design principles with the modern
Material Design guidelines in mind.

## Compatibility
- **Minimum SDK:** 31 (Android 12)
- **Target SDK:** 36 (Android 16)
- **Device Types Supported:** Phones and Tablets
- **Orientation Supported**: Portrait

## Features
The following features are added or improved upon compared to the official app:

- Overall better error and permission handling
- Automatic reconnection attempt when the connection drops while connecting
- Ability to cancel an ongoing connection attempt
- Ability to favor devices for quicker access
- Offers vastly greater number of color combinations by using an HSV color picker instead of a 
limited hue picker
- Ability to match the LEDs color with device's theme
- Ability to pick a random color
- Improved UI and UX designed using the latest Material Design guidelines, with supporting dynamic
themes

> [!NOTE]
> The app is still in development, thus the following features that are in the official app
> are currently missing here.
> - Choosing from the available 120 light effects and favoring them
> - Ability to rename the controller
> - Ability to control specific amount of pixels on the LED strip
> - Ability to set RGB sequence and IC type