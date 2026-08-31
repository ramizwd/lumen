package com.example.lumen.presentation.ble.ledcontrol.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.lumen.R
import com.example.lumen.domain.ble.model.IcModel
import com.example.lumen.domain.ble.model.RgbSequence
import com.example.lumen.presentation.common.components.WheelPicker
import com.example.lumen.presentation.common.utils.DeviceConfiguration
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing

@Composable
fun HardwareConfigDialog(
    deviceConfig: DeviceConfiguration,
    currentIcModel: IcModel,
    currentRgbSeq: RgbSequence,
    onIcModelChange: (IcModel) -> Unit,
    onRgbSeqChange: (RgbSequence) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.hardware_configuration),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            HardwareConfigDialogContent(
                deviceConfig = deviceConfig,
                currentIcModel = currentIcModel,
                currentRgbSeq = currentRgbSeq,
                onIcModelChange = onIcModelChange,
                onRgbSeqChange = onRgbSeqChange,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.done))
            }
        },
    )
}

@Composable
private fun HardwareConfigDialogContent(
    deviceConfig: DeviceConfiguration,
    currentIcModel: IcModel,
    currentRgbSeq: RgbSequence,
    onIcModelChange: (IcModel) -> Unit,
    onRgbSeqChange: (RgbSequence) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.rgb_sequence),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                when (deviceConfig) {
                    DeviceConfiguration.TABLET_PORTRAIT,
                    DeviceConfiguration.TABLET_LANDSCAPE,
                    DeviceConfiguration.MOBILE_PORTRAIT,
                    -> {
                        WheelPicker(
                            items = RgbSequence.entries,
                            initialIndex = RgbSequence.entries.indexOf(currentRgbSeq),
                            onItemSelected = onRgbSeqChange,
                            labelExtractor = { it.name },
                        )
                    }
                    DeviceConfiguration.MOBILE_LANDSCAPE -> {
                        WheelPicker(
                            visibleItemsCount = 3,
                            items = RgbSequence.entries,
                            initialIndex = RgbSequence.entries.indexOf(currentRgbSeq),
                            onItemSelected = onRgbSeqChange,
                            labelExtractor = { it.name },
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.ic_model),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                when (deviceConfig) {
                    DeviceConfiguration.TABLET_PORTRAIT,
                    DeviceConfiguration.TABLET_LANDSCAPE,
                    DeviceConfiguration.MOBILE_PORTRAIT,
                    -> {
                        WheelPicker(
                            items = IcModel.entries,
                            initialIndex = IcModel.entries.indexOf(currentIcModel),
                            onItemSelected = onIcModelChange,
                            labelExtractor = { it.name },
                        )
                    }
                    DeviceConfiguration.MOBILE_LANDSCAPE -> {
                        WheelPicker(
                            visibleItemsCount = 3,
                            items = IcModel.entries,
                            initialIndex = IcModel.entries.indexOf(currentIcModel),
                            onItemSelected = onIcModelChange,
                            labelExtractor = { it.name },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HardwareConfigDialogContentPreview() {
    LumenTheme {
        Surface {
            HardwareConfigDialogContent(
                deviceConfig = DeviceConfiguration.MOBILE_PORTRAIT,
                currentIcModel = IcModel.SK6812_RGBW,
                currentRgbSeq = RgbSequence.RGB,
                onIcModelChange = { },
                onRgbSeqChange = { },
            )
        }
    }
}
