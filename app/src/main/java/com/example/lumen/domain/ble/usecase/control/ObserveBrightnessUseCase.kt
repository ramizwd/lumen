package com.example.lumen.domain.ble.usecase.control

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.sample
import javax.inject.Inject

@OptIn(FlowPreview::class)
class ObserveBrightnessUseCase @Inject constructor() {
    operator fun invoke(flow: Flow<Float>) =
        flow.sample(250)
}