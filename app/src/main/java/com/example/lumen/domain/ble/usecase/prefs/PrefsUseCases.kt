package com.example.lumen.domain.ble.usecase.prefs

import javax.inject.Inject

/**
 * Wrapper for user preference use cases
 */
data class PrefsUseCases @Inject constructor(
    val getFavoriteDeviceAddressesUseCase: GetFavDeviceAddressesUseCase,
    val addFavoriteDeviceAddressUseCase: AddFavDeviceAddressUseCase,
    val removeFavoriteDeviceAddressUseCase: RemoveFavDeviceAddressUseCase,
    val getDeviceListPreferenceUseCase: GetDeviceListPreferenceUseCase,
    val saveDeviceListPreferenceUseCase: SaveDeviceListPreferenceUseCase,
)