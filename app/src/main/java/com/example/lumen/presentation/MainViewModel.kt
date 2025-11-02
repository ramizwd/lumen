package com.example.lumen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumen.domain.ble.model.BluetoothState
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.domain.ble.usecase.common.ObserveBluetoothStateUseCase
import com.example.lumen.domain.ble.usecase.connection.ConnectionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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
): ViewModel() {

    companion object {
        private const val LOG_TAG = "MainViewModel"
    }

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    init {
        viewModelScope.launch {
            connectionUseCases.observeConnectionStateUseCase().collect { state ->
                _connectionState.update { state }
            }
        }

        observeBluetoothStateUseCase().onEach { btState ->
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

                    if (_connectionState.value == ConnectionState.STATE_LOADED_AND_CONNECTED) {
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

