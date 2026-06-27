package dr.achim.sleep_timer.presentation.hue

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.findActivity
import dr.achim.sleep_timer.data.remote.hue.HueBridge
import dr.achim.sleep_timer.ui.components.EmptyState
import dr.achim.sleep_timer.ui.components.FullScreenLoadingState
import dr.achim.sleep_timer.ui.components.LoadingIndicator
import dr.achim.sleep_timer.ui.components.SectionTitle
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens

@Composable
fun HueDiscoveryScreen(
    onBack: () -> Unit,
    viewModel: HueDiscoveryViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissions = viewModel.getNearbyPermissions()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.all { it.value }) {
            viewModel.onAction(HueDiscoveryUiAction.PermissionGranted)
        } else {
            val activity = context.findActivity()
            val shouldShowRationale = permissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
            viewModel.onAction(HueDiscoveryUiAction.PermissionDenied(shouldShowRationale))
        }
    }

    LifecycleResumeEffect(Unit) {
        if (viewModel.hasNearbyPermission()) {
            viewModel.onAction(HueDiscoveryUiAction.PermissionGranted)
        }
        onPauseOrDispose {}
    }

    LaunchedEffect(uiState) {
        if (uiState is HueDiscoveryUiState.PermissionDenied) {
            launcher.launch(permissions)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is HueNavEvent.LinkingSuccess -> onBack()
            }
        }
    }

    HueDiscoveryContent(
        uiState = uiState,
        onBack = onBack,
        onAction = viewModel::onAction,
        onRequestPermission = { launcher.launch(permissions) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HueDiscoveryContent(
    uiState: HueDiscoveryUiState,
    onBack: () -> Unit,
    onAction: (HueDiscoveryUiAction) -> Unit,
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current
    var showManualIpDialog by remember { mutableStateOf(false) }
    var showUnlinkConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hue_discovery_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.settings_back_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState !is HueDiscoveryUiState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(onClick = { onAction(HueDiscoveryUiAction.DiscoverBridges) }) {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.hue_discovery_refresh)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(AppTheme.dimens.spacingMedium),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = uiState, label = "HueDiscoveryState") { state ->
                when (state) {
                    is HueDiscoveryUiState.Loading -> {
                        FullScreenLoadingState(text = stringResource(R.string.hue_discovery_searching))
                    }

                    is HueDiscoveryUiState.PermissionDenied -> {
                        val textRes = if (state.shouldShowRationale) {
                            R.string.hue_discovery_permission_grant
                        } else {
                            R.string.hue_discovery_permission_open_settings
                        }

                        EmptyState(
                            title = stringResource(R.string.hue_discovery_permission_title),
                            subtitle = stringResource(R.string.hue_discovery_permission_subtitle),
                            imageRes = R.drawable.img_no_results,
                            action = {
                                TextButton(
                                    onClick = {
                                        if (state.shouldShowRationale) {
                                            onRequestPermission()
                                        } else {
                                            openAppSettings(context)
                                        }
                                    }
                                ) {
                                    Text(stringResource(textRes))
                                }
                            }
                        )
                    }

                    is HueDiscoveryUiState.Empty -> {
                        EmptyState(
                            title = stringResource(R.string.hue_discovery_no_bridges),
                            subtitle = stringResource(R.string.hue_discovery_no_bridges_sub),
                            imageRes = R.drawable.img_no_results,
                            action = {
                                TextButton(onClick = { showManualIpDialog = true }) {
                                    Text(stringResource(R.string.hue_discovery_manual_ip))
                                }
                            }
                        )
                    }

                    is HueDiscoveryUiState.Display -> {
                        SuccessContent(
                            data = state.data,
                            onLink = { onAction(HueDiscoveryUiAction.StartLinking(it)) },
                            onUnlink = { showUnlinkConfirmDialog = true },
                            onManualIpClick = { showManualIpDialog = true }
                        )
                    }

                    is HueDiscoveryUiState.Linking -> {
                        SuccessContent(
                            data = state.data,
                            onLink = { onAction(HueDiscoveryUiAction.StartLinking(it)) },
                            onUnlink = { showUnlinkConfirmDialog = true },
                            onManualIpClick = { showManualIpDialog = true }
                        )
                        LinkingDialog(
                            onDismiss = { onAction(HueDiscoveryUiAction.CancelLinking) },
                            onConfirm = { onAction(HueDiscoveryUiAction.LinkBridge) },
                            error = state.error
                        )
                    }
                }
            }
        }
    }

    if (showManualIpDialog) {
        ManualIpDialog(
            onDismiss = { showManualIpDialog = false },
            onConfirm = { ip: String ->
                showManualIpDialog = false
                onAction(HueDiscoveryUiAction.ManualIpDiscovery(ip))
            }
        )
    }

    if (showUnlinkConfirmDialog) {
        UnlinkConfirmDialog(
            onDismiss = { showUnlinkConfirmDialog = false },
            onConfirm = {
                showUnlinkConfirmDialog = false
                onAction(HueDiscoveryUiAction.UnlinkBridge)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SuccessContent(
    data: HueDiscoveryData,
    onLink: (HueBridge) -> Unit,
    onUnlink: () -> Unit,
    onManualIpClick: () -> Unit
) {
    if (data.bridges.isEmpty() && data.isSearching) {
        FullScreenLoadingState(text = stringResource(R.string.hue_discovery_searching))
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingExtraExtraSmall)
    ) {
        if (data.bridges.isNotEmpty()) {
            item {
                SectionTitle(
                    text = pluralStringResource(
                        R.plurals.hue_discovery_devices_count_title,
                        data.bridges.size,
                        data.bridges.size
                    ),
                )
            }
            items(data.bridges, key = { it.ipAddress }) { bridge ->
                BridgeItem(
                    bridge = bridge,
                    isPaired = bridge.ipAddress == data.pairedBridgeIp,
                    onLink = { onLink(bridge) },
                    onUnlink = onUnlink
                )
            }
        }

        item {
            Spacer(Modifier.height(AppTheme.dimens.spacingNormal))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onManualIpClick) {
                    Text(stringResource(R.string.hue_discovery_manual_ip))
                }
            }
        }

        if (data.isSearching) {
            item {
                LoadingIndicator(text = stringResource(R.string.hue_discovery_searching))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BridgeItem(bridge: HueBridge, isPaired: Boolean, onLink: () -> Unit, onUnlink: () -> Unit) {
    ListItem(
        onClick = if (isPaired) onUnlink else onLink,
        supportingContent = {
            Text(text = bridge.ipAddress)
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_network_node),
                contentDescription = null,
            )
        },
        trailingContent = {
            TextButton(onClick = if (isPaired) onUnlink else onLink) {
                Text(
                    text = if (isPaired) stringResource(R.string.hue_discovery_unlink) else stringResource(
                        R.string.hue_discovery_link
                    ),
                    color = if (isPaired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        val name = bridge.name ?: stringResource(R.string.hue_discovery_bridge_default_name)
        Text(text = name)
    }
}

@Composable
private fun LinkingDialog(onDismiss: () -> Unit, onConfirm: () -> Unit, error: String?) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hue_linking_title)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.img_pushlink_bridge),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(Modifier.height(AppTheme.dimens.spacingNormal))
                Text(
                    text = error ?: stringResource(R.string.hue_linking_message),
                    textAlign = TextAlign.Center,
                    color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.hue_linking_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.hue_linking_cancel))
            }
        }
    )
}

@Composable
private fun ManualIpDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var ip by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hue_manual_ip_title)) },
        text = {
            OutlinedTextField(
                value = ip,
                onValueChange = { ip = it },
                label = { Text(stringResource(R.string.hue_manual_ip_label)) },
                placeholder = { Text("192.168.1.100") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(ip) }, enabled = ip.isNotBlank()) {
                Text(stringResource(R.string.hue_manual_ip_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.hue_manual_ip_cancel))
            }
        }
    )
}

@Composable
private fun UnlinkConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hue_discovery_unlink_title)) },
        text = { Text(stringResource(R.string.hue_discovery_unlink_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.hue_discovery_unlink_confirm),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.hue_discovery_unlink_cancel))
            }
        }
    )
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
private fun HueDiscoveryLoadingPreview() {
    AppTheme {
        HueDiscoveryContent(
            uiState = HueDiscoveryUiState.Loading,
            onBack = {},
            onAction = {},
            onRequestPermission = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HueDiscoveryPermissionPreview() {
    AppTheme {
        HueDiscoveryContent(
            uiState = HueDiscoveryUiState.PermissionDenied(shouldShowRationale = true),
            onBack = {},
            onAction = {},
            onRequestPermission = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HueDiscoveryEmptyPreview() {
    AppTheme {
        HueDiscoveryContent(
            uiState = HueDiscoveryUiState.Empty,
            onBack = {},
            onAction = {},
            onRequestPermission = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HueDiscoveryDisplayPreview() {
    AppTheme {
        HueDiscoveryContent(
            uiState = HueDiscoveryUiState.Display(
                data = HueDiscoveryData(
                    bridges = listOf(
                        HueBridge(name = "Hue Bridge", ipAddress = "192.168.1.100"),
                        HueBridge(name = "Hue Bridge", ipAddress = "192.168.1.101")
                    ),
                    pairedBridgeIp = "192.168.1.102",
                    isSearching = true
                )
            ),
            onBack = {},
            onAction = {},
            onRequestPermission = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HueDiscoveryLinkingPreview() {
    AppTheme {
        HueDiscoveryContent(
            uiState = HueDiscoveryUiState.Linking(
                data = HueDiscoveryData(
                    bridges = listOf(
                        HueBridge(name = "Hue Bridge", ipAddress = "192.168.1.100")
                    )
                ),
                bridge = HueBridge(name = "Hue Bridge", ipAddress = "192.168.1.100")
            ),
            onBack = {},
            onAction = {},
            onRequestPermission = {}
        )
    }
}
