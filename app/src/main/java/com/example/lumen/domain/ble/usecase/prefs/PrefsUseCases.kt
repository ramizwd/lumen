package com.example.lumen.domain.ble.usecase.prefs

import javax.inject.Inject

/**
 * Wrapper for user preference use cases
 */
data class PrefsUseCases @Inject constructor(
    val getFavoriteDeviceAddressesUseCase: GetFavDeviceAddressesUseCase,
    val addFavDeviceAddressUseCase: AddFavDeviceAddressUseCase,
    val removeFavDeviceAddressUseCase: RemoveFavDeviceAddressUseCase,
    val getDeviceListPreferenceUseCase: GetDeviceListPreferenceUseCase,
    val saveDeviceListPreferenceUseCase: SaveDeviceListPreferenceUseCase,
    val getFavEffectsUseCase: GetFavEffectsUseCase,
    val addFavEffectUseCase: AddFavEffectUseCase,
    val removeFavEffectUseCase: RemoveFavEffectUseCase,
)
