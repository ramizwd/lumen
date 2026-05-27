package com.example.lumen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.R
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.usecase.common.ObserveBluetoothStateUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import com.example.lumen.presentation.common.model.LoadingInfo
import com.example.lumen.presentation.common.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Main ViewModel for managing global states
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectionUseCases: ConnectionUseCases,
    observeBluetoothStateUseCase: ObserveBluetoothStateUseCase,
) : ViewModel() {
    companion object {
        private const val LOG_TAG = "MainViewModel"
    }

    val connectionState = connectionUseCases
        .observeConnectionStateUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ConnectionState.DISCONNECTED,
        )

    val loadingInfo = connectionState
        .map { state ->
            val show = when (state) {
                ConnectionState.CONNECTING,
                ConnectionState.LOADING_DEVICE_STATE,
                ConnectionState.INVALID_DEVICE,
                ConnectionState.RETRYING,
                -> true
                else -> false
            }

            val text = when (state) {
                ConnectionState.CONNECTING ->
                    UiText.StringResource(R.string.connecting)
                ConnectionState.LOADING_DEVICE_STATE ->
                    UiText.StringResource(R.string.initializing)
                ConnectionState.RETRYING ->
                    UiText.StringResource(R.string.connection_failed_retrying)
                ConnectionState.INVALID_DEVICE ->
                    UiText.StringResource(R.string.invalid_device_disconnecting)
                else -> null
            }

            LoadingInfo(show, text)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LoadingInfo(false, null),
        )

    init {
        observeBluetoothStateUseCase()
            .onEach { btState ->
                when (btState) {
                    BluetoothState.ON -> {
                        Timber.tag(LOG_TAG).d("BT on")
                    }
                    BluetoothState.OFF -> {
                        Timber.tag(LOG_TAG).d("BT off")
                    }
                    BluetoothState.TURNING_ON -> {
                        Timber.tag(LOG_TAG).d("BT turning on...")
                    }
                    BluetoothState.TURNING_OFF -> {
                        Timber.tag(LOG_TAG).d("BT turning off...")

                        if (connectionState.value == ConnectionState.STATE_LOADED_AND_CONNECTED) {
                            Timber.tag(LOG_TAG).i("Disconnecting...")
                            disconnect()
                        }
                    }
                    BluetoothState.UNKNOWN -> {
                        Timber.tag(LOG_TAG).d("BT state unknown")
                    }
                }
            }.catch { throwable ->
                Timber.tag(LOG_TAG).e(throwable, "BT state observation error")
            }.launchIn(viewModelScope)
    }

    fun disconnect() {
        viewModelScope.launch {
            connectionUseCases.disconnectUseCase()
        }
    }
}
