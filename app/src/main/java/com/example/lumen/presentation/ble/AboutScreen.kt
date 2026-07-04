package com.example.lumen.presentation.ble

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.lumen.BuildConfig
import com.example.lumen.R
import com.example.lumen.presentation.theme.LumenTheme
import com.example.lumen.presentation.theme.spacing
import com.example.lumen.utils.AppConstants.SOURCE_CODE_URL
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val openLicenseDialog = rememberSaveable { mutableStateOf(false) }

    val libraries by produceLibraries()
    val uriHandler = LocalUriHandler.current
    val resources = LocalResources.current

    val licenseText = remember(resources) {
        resources
            .openRawResource(R.raw.gpl_3_0)
            .bufferedReader()
            .use { it.readText() }
    }

    when {
        openLicenseDialog.value -> {
            LicenseDialog(
                licenseText = licenseText,
                onDismissRequest = { openLicenseDialog.value = false },
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.navigate_back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        AboutContent(
            libraries = libraries,
            uriHandler = uriHandler,
            openLicenseDialog = openLicenseDialog,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun AboutContent(
    libraries: Libs?,
    uriHandler: UriHandler,
    openLicenseDialog: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    LibrariesContainer(
        libraries,
        modifier = modifier.fillMaxSize(),
        header = {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallIncreased))

                    Text(
                        text = stringResource(
                            R.string.version,
                            BuildConfig.VERSION_NAME,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                    Row(
                        horizontalArrangement = Arrangement
                            .spacedBy(MaterialTheme.spacing.smallIncreased),
                    ) {
                        OutlinedButton(
                            onClick = { uriHandler.openUri(SOURCE_CODE_URL) },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.code_24px),
                                contentDescription = stringResource(R.string.view_source_code),
                            )

                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                            Text(
                                text = stringResource(R.string.source_code),
                                style = TextStyle(textDecoration = TextDecoration.Underline),
                            )
                        }

                        OutlinedButton(
                            onClick = { openLicenseDialog.value = true },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.contract_24px),
                                contentDescription = stringResource(R.string.view_license),
                            )

                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                            Text(stringResource(R.string.license))
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.largeIncreased))

                    HorizontalDivider(
                        Modifier,
                        DividerDefaults.Thickness,
                    )

                    Text(
                        text = stringResource(R.string.list_of_libraries),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.large),
                    )
                }
            }
        },
    )
}

@Composable
private fun LicenseDialog(
    licenseText: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier
            .widthIn(max = 700.dp)
            .heightIn(max = 600.dp)
            .padding(horizontal = 18.dp),
        title = {
            Text(
                text = stringResource(R.string.license),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = licenseText,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                softWrap = false,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState),
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = { },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(stringResource(R.string.dismiss))
            }
        },
    )
}

@PreviewLightDark
@Composable
fun AboutScreenPreview() {
    LumenTheme {
        Surface {
            AboutScreen(
                onBackClick = {},
            )
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun DropdownMenuLandscapePreview() {
    LumenTheme {
        Surface {
            AboutScreen(
                onBackClick = {},
            )
        }
    }
}

@Preview(widthDp = 1200, heightDp = 800)
@Composable
fun DropdownMenuTabletLandscapePreview() {
    LumenTheme {
        Surface {
            AboutScreen(
                onBackClick = {},
            )
        }
    }
}
