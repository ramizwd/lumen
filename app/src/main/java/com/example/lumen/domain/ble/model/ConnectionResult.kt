package com.example.lumen.domain.ble.model

/**
 * Sealed interface for GATT results and errors
 */
sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult

    object Disconnected : ConnectionResult

    object InvalidDevice : ConnectionResult

    object ConnectionCanceled : ConnectionResult

    sealed interface Failure : ConnectionResult {
        data object PermsMissing : Failure

        data object BtDisabled : Failure

        data object DeviceNotFound : Failure

        data object CommandFailed : Failure

        data object CannotRetry : Failure

        data object ConnectionFailed : Failure
    }
}
