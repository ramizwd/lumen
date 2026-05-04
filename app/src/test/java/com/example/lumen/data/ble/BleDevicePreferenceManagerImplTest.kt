package com.example.lumen.data.ble

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.example.lumen.domain.ble.model.DeviceListType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Unit tests for [BleDevicePreferenceManagerImpl]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BleDevicePreferenceManagerImplTest {

    private val address = "00:11:22:33:44:55"

    @TempDir
    private lateinit var tempDir: File

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var mockDataStore: DataStore<Preferences>
    private lateinit var testManager: BleDevicePreferenceManagerImpl
    private lateinit var mockManager: BleDevicePreferenceManagerImpl
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val testFile = File(tempDir, "test.preferences_pb")
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testFile }
        )

        mockDataStore = mockk()

        testManager = BleDevicePreferenceManagerImpl(testDataStore)
        mockManager = BleDevicePreferenceManagerImpl(mockDataStore)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addFavDeviceAddress stores address correctly`() = runTest {
        testManager.addFavDeviceAddress(address)

        testManager.getFavDeviceAddresses().test {
            assert(awaitItem().contains(address))
        }
    }

    @Test
    fun `addFavDeviceAddress should not allow duplicates`() = runTest {
        testManager.addFavDeviceAddress(address)
        testManager.addFavDeviceAddress(address)

        testManager.getFavDeviceAddresses().test {
            assertEquals(1, awaitItem().size)
        }
    }

    @Test
    fun `addFavDeviceAddress should handle write exceptions`() = runTest {
        coEvery { mockDataStore.updateData(any()) } throws IOException("Write failed")

        mockManager.addFavDeviceAddress(address)
    }

    @Test
    fun `removeFavDeviceAddress removes existing address`() = runTest {
        testManager.addFavDeviceAddress(address)

        testManager.removeFavDeviceAddress(address)

        testManager.getFavDeviceAddresses().test {
            assertEquals(emptySet<String>(), awaitItem())
        }
    }

    @Test
    fun `removeFavDeviceAddress should handle edit exceptions`() = runTest {
        coEvery { mockDataStore.updateData(any()) } throws IOException("Edit failed")

        mockManager.removeFavDeviceAddress(address)
    }

    @Test
    fun `saveDeviceListPreference updates selection`() = runTest {
        val pref = DeviceListType.FAVORITE_DEVICES

        testManager.getDeviceListPreference().test {
            assertEquals(DeviceListType.ALL_DEVICES, awaitItem())

            testManager.saveDeviceListPreference(pref)

            assertEquals(pref, awaitItem())
        }
    }

    @Test
    fun `getFavDeviceAddresses should emit empty prefs on exception`() = runTest {
        every { mockDataStore.data } returns flow {
            throw IllegalStateException("Unexpected error")
        }

        mockManager.getFavDeviceAddresses().test {
            assertEquals(emptySet<String>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getDeviceListPreference should emit empty prefs on exception`() = runTest {
        every { mockDataStore.data } returns flow {
            throw IllegalStateException("Unexpected error")
        }

        mockManager.getDeviceListPreference().test {
            assertEquals(DeviceListType.ALL_DEVICES, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `saveDeviceListPreference should handle edit exceptions`() = runTest {
        coEvery { mockDataStore.updateData(any()) } throws IOException("Edit failed")

        mockManager.saveDeviceListPreference(DeviceListType.ALL_DEVICES)
    }
}