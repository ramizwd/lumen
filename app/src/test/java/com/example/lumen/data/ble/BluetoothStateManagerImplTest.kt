package com.example.lumen.data.ble

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.cash.turbine.test
import com.example.lumen.domain.ble.model.BluetoothState
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unite tests for [BluetoothStateManagerImpl]
 */
class BluetoothStateManagerImplTest {

    private val receiverSlot = slot<BroadcastReceiver>()

    private lateinit var context: Context
    private lateinit var btAdapter: BluetoothAdapter

    @BeforeEach
    fun setUp() {
        context = mockk()
        btAdapter = mockk()

        every { context.registerReceiver(capture(receiverSlot), any(), any()) }
        every {
            context.registerReceiver(capture(receiverSlot), any())
        } returns mockk()
    }

    @Test
    fun `initial state reflects bluetoothAdapter state`() = runTest {
        every { btAdapter.state } returns BluetoothAdapter.STATE_ON

        val manager = BluetoothStateManagerImpl(context, btAdapter)

        manager.bluetoothState.test {
            assertEquals(BluetoothState.ON, awaitItem())
        }
    }

    @Test
    fun `receiver updates flow when BT state changes`() = runTest {
        every { btAdapter.state } returns BluetoothAdapter.STATE_OFF

        val manager = BluetoothStateManagerImpl(context, btAdapter)

        val intent = mockk<Intent>(relaxed = true)
        every { intent.action } returns BluetoothAdapter.ACTION_STATE_CHANGED
        every {
            intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, any())
        } returns BluetoothAdapter.STATE_ON

        receiverSlot.captured.onReceive(context, intent)

        manager.bluetoothState.test {
            assertEquals(BluetoothState.ON, awaitItem())
        }
    }

    @Test
    fun `verify receiver is registered on init`() {
        every { btAdapter.state } returns BluetoothAdapter.STATE_OFF

        BluetoothStateManagerImpl(context, btAdapter)

        verify { context.registerReceiver(any(), any()) }
    }
}