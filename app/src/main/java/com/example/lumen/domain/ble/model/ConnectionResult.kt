package com.example.lumen.domain.ble.model

/**
 * Sealed interface for GATT results and errors
 */
sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    object Disconnected: ConnectionResult

    data class Error(val message: String): ConnectionResult
    data class ConnectionFailed(val message: String): ConnectionResult
}
