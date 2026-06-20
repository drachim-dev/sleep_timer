package dr.achim.sleep_timer.presentation.settings

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.BuildConfig
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.Constants.extendOnShakeSteps
import dr.achim.sleep_timer.common.findActivity
import dr.achim.sleep_timer.model.Product
import dr.achim.sleep_timer.model.PurchaseEvent
import dr.achim.sleep_timer.model.ThemeMode
import dr.achim.sleep_timer.receiver.SleepTimerAdminReceiver
import dr.achim.sleep_timer.ui.components.SectionTitle
import dr.achim.sleep_timer.ui.components.SwitchListItem
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToCredits: () -> Unit,
    onNavigateToFaq: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val glowEnabled by viewModel.glowEffectEnabled.collectAsStateWithLifecycle()
    val glowIntensity by viewModel.glowIntensity.collectAsStateWithLifecycle()
    val extendOnShake by viewModel.extendOnShake.collectAsStateWithLifecycle()
    val extendOnShakeMinutes by viewModel.extendOnShakeMinutes.collectAsStateWithLifecycle()
    val isDeviceAdminEnabled by viewModel.isDeviceAdminEnabled.collectAsStateWithLifecycle()
    val hasNotificationAccess by viewModel.hasNotificationAccess.collectAsStateWithLifecycle()
    val productUiModels by viewModel.productUiModels.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.onAction(SettingsUiAction.RefreshDeviceAdminStatus)
        onPauseOrDispose { }
    }

    SettingsScreenContent(
        onBack = onBack,
        onNavigateToCredits = onNavigateToCredits,
        onNavigateToFaq = onNavigateToFaq,
        themeMode = themeMode,
        glowEnabled = glowEnabled,
        glowIntensity = glowIntensity,
        extendOnShake = extendOnShake,
        extendOnShakeMinutes = extendOnShakeMinutes,
        isDeviceAdminEnabled = isDeviceAdminEnabled,
        hasNotificationAccess = hasNotificationAccess,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        purchaseEvents = viewModel.events,
        productUiModels = productUiModels,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    onBack: () -> Unit,
    onNavigateToCredits: () -> Unit,
    onNavigateToFaq: () -> Unit,
    themeMode: ThemeMode,
    glowEnabled: Boolean,
    glowIntensity: Float,
    extendOnShake: Boolean,
    extendOnShakeMinutes: Int,
    isDeviceAdminEnabled: Boolean,
    hasNotificationAccess: Boolean,
    onAction: (SettingsUiAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    purchaseEvents: Flow<PurchaseEvent>,
    productUiModels: List<StoreProductUiModel>
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val deviceAdminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            onAction(SettingsUiAction.RefreshDeviceAdminStatus)
        }
    )

    val notificationAccessLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            onAction(SettingsUiAction.RefreshDndStatus(true))
        }
    )

    var showThemeDialog by rememberSaveable { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentThemeMode = themeMode,
            onThemeModeSelected = {
                onAction(SettingsUiAction.SetThemeMode(it))
                showThemeDialog = false
            },
            onDismissRequest = { showThemeDialog = false }
        )
    }

    var showConfetti by remember { mutableStateOf(false) }
    val purchaseErrorMessage = stringResource(R.string.error_purchase_failure)
    LaunchedEffect(Unit) {
        purchaseEvents.collectLatest {
            when (it) {
                PurchaseEvent.PurchaseComplete -> {
                    showConfetti = true
                    // TODO
                    // show in app review
                }

                PurchaseEvent.PurchaseAborted -> {
                    snackbarHostState.showSnackbar(purchaseErrorMessage)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings_title)) },
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
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding)
                    .padding(AppTheme.dimens.spacingMedium),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingLarge)
            ) {
                SettingsSection(
                    title = { SectionTitle(stringResource(R.string.settings_section_appearance)) }
                ) {
                    SettingsItem(
                        painter = painterResource(R.drawable.ic_palette),
                        title = stringResource(R.string.settings_theme_title),
                        subtitle = when (themeMode) {
                            ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_mode_system)
                            ThemeMode.LIGHT -> stringResource(R.string.settings_theme_mode_light)
                            ThemeMode.DARK -> stringResource(R.string.settings_theme_mode_dark)
                            ThemeMode.DYNAMIC -> stringResource(R.string.settings_theme_mode_dynamic)
                        },
                        onClick = { showThemeDialog = true }
                    )
                    SettingsSwitchItem(
                        painter = painterResource(R.drawable.ic_blur),
                        title = stringResource(R.string.settings_glow_effect_title),
                        subtitle = stringResource(R.string.settings_glow_effect_subtitle),
                        checked = glowEnabled,
                        onCheckedChange = { onAction(SettingsUiAction.SetGlowEffectEnabled(it)) }
                    )
                    AnimatedVisibility(glowEnabled) {
                        SettingsSliderItem(
                            title = stringResource(R.string.settings_glow_intensity_title),
                            value = glowIntensity,
                            onValueChange = { onAction(SettingsUiAction.SetGlowIntensity(it)) },
                            valueRange = 20f..60f
                        )
                    }
                }

                SettingsSection(
                    title = { SectionTitle(stringResource(R.string.settings_section_timer_settings)) }
                ) {
                    SettingsSwitchItem(
                        painter = painterResource(R.drawable.ic_vibrate),
                        title = stringResource(R.string.settings_extend_on_shake_title),
                        checked = extendOnShake,
                        onCheckedChange = { onAction(SettingsUiAction.SetExtendOnShake(it)) }
                    )
                    AnimatedVisibility(extendOnShake) {
                        val currentIndex = extendOnShakeSteps.indexOf(extendOnShakeMinutes).coerceAtLeast(0)
                        SettingsSliderItem(
                            title = stringResource(
                                R.string.settings_extend_on_shake_minutes_title,
                                extendOnShakeMinutes
                            ),
                            value = currentIndex.toFloat(),
                            onValueChange = { index ->
                                val minutes = extendOnShakeSteps[index.toInt()]
                                onAction(SettingsUiAction.SetExtendOnShakeMinutes(minutes))
                            },
                            valueRange = 0f..(extendOnShakeSteps.size - 1).toFloat(),
                            steps = extendOnShakeSteps.size - 2
                        )
                    }
                }

                SettingsSection(
                    title = { SectionTitle(stringResource(R.string.settings_section_support_me)) }
                ) {
                    SettingsItem(
                        painter = painterResource(R.drawable.ic_rate_review),
                        title = stringResource(R.string.settings_like_app_title),
                        subtitle = stringResource(R.string.settings_like_app_subtitle),
                        trailingText = stringResource(R.string.settings_like_app_trailing),
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".toUri()
                                setPackage("com.android.vending")
                            }
                            context.startActivity(intent)
                        }
                    )

                    productUiModels.forEach { uiModel ->
                        val product = uiModel.product
                        val icon = when (product.id) {
                            Product.Donation.id -> R.drawable.ic_coffee
                            Product.RemoveAds.id -> R.drawable.ic_ad_off
                            else -> null
                        }

                        icon?.let {
                            SettingsItem(
                                painter = painterResource(icon),
                                title = uiModel.product.title,
                                subtitle = uiModel.product.description,
                                trailingText = if (uiModel.isPurchased) stringResource(R.string.settings_remove_ads_trailing) else product.price.formatted,
                                trailingColor = Color(0xFF81C784),
                                onClick = {
                                    onAction(SettingsUiAction.PurchaseProduct(activity, product))
                                }
                            )
                        }
                    }
                }

                SettingsSection(
                    title = { SectionTitle(stringResource(R.string.settings_section_advanced)) }
                ) {
                    val adminDescription =
                        stringResource(R.string.settings_device_admin_description)
                    SettingsSwitchItem(
                        painter = painterResource(R.drawable.ic_device_admin),
                        title = stringResource(R.string.settings_device_admin_title),
                        subtitle = stringResource(R.string.settings_device_admin_subtitle),
                        checked = isDeviceAdminEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                val intent =
                                    Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                        putExtra(
                                            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                            ComponentName(
                                                context,
                                                SleepTimerAdminReceiver::class.java
                                            )
                                        )
                                        putExtra(
                                            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                            adminDescription
                                        )
                                    }
                                deviceAdminLauncher.launch(intent)
                            } else {
                                onAction(SettingsUiAction.DisableDeviceAdmin)
                            }
                        }
                    )

                    SettingsSwitchItem(
                        painter = painterResource(R.drawable.ic_notification_settings),
                        title = stringResource(R.string.settings_notification_access_title),
                        subtitle = stringResource(R.string.settings_notification_access_subtitle),
                        checked = hasNotificationAccess,
                        onCheckedChange = { enabled ->
                            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                            notificationAccessLauncher.launch(intent)
                            if (!enabled) {
                                onAction(SettingsUiAction.DisableNotificationAccess)
                            }
                        }
                    )
                }

                SettingsSection(
                    title = { SectionTitle(stringResource(R.string.settings_section_other)) }
                ) {
                    SettingsItem(
                        painter = painterResource(R.drawable.ic_help),
                        title = stringResource(R.string.settings_faq_title),
                        onClick = onNavigateToFaq
                    )
                    SettingsItem(
                        painter = painterResource(R.drawable.ic_license),
                        title = stringResource(R.string.settings_credits_title),
                        onClick = onNavigateToCredits
                    )
                }
            }
        }

        if (showConfetti) {
            Confetti(modifier = Modifier.fillMaxSize()) { showConfetti = false }
        }
    }
}

@Composable
fun Confetti(modifier: Modifier, onAnimationCompleted: () -> Unit) {
    val baseParty = Party(
        spread = 45,
        speed = 30f,
        maxSpeed = 50f,
        damping = 0.9f,
        emitter = Emitter(duration = 100.milliseconds).max(100)
    )

    ConfettiKit(
        modifier = modifier,
        parties = listOf(
            baseParty.copy(angle = 315, position = Position.Relative(0.0, 1.0)),
            baseParty.copy(angle = 225, position = Position.Relative(1.0, 1.0))
        ),
        onParticleSystemEnded = { _, activeSystems ->
            if (activeSystems == 0) {
                onAnimationCompleted()
            }
        }
    )
}

@Composable
fun SettingsSection(title: @Composable () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column {
        title()
        Column(
            modifier = Modifier.clip(MaterialTheme.shapes.large),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingExtraExtraSmall),
            content = content
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimens.spacingMedium),
        ) {
            Column(
                modifier = Modifier
                    .padding(AppTheme.dimens.spacingLarge)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.settings_theme_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = AppTheme.dimens.spacingMedium)
                )

                ThemeMode.entries
                    .filter { it != ThemeMode.DYNAMIC || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }
                    .forEach { mode ->
                    val label = when (mode) {
                        ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_mode_system)
                        ThemeMode.LIGHT -> stringResource(R.string.settings_theme_mode_light)
                        ThemeMode.DARK -> stringResource(R.string.settings_theme_mode_dark)
                        ThemeMode.DYNAMIC -> stringResource(R.string.settings_theme_mode_dynamic)
                    }

                    val interactionSource = remember { MutableInteractionSource() }
                    ListItem(
                        modifier = Modifier.selectable(
                            selected = currentThemeMode == mode,
                            onClick = { onThemeModeSelected(mode) },
                            role = Role.RadioButton,
                            interactionSource = interactionSource,
                            indication = ripple()
                        ),
                        headlineContent = { Text(text = label) },
                        leadingContent = {
                            RadioButton(
                                selected = currentThemeMode == mode,
                                onClick = null,
                                interactionSource = interactionSource
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacingMedium))
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    painter: Painter,
    title: String,
    subtitle: String? = null,
    trailingText: String? = null,
    trailingColor: Color = Color.LightGray,
    onClick: () -> Unit
) {
    ListItem(
        onClick = onClick,
        supportingContent = subtitle?.let { { Text(text = it) } },
        leadingContent = {
            Icon(
                painter = painter,
                contentDescription = null,
            )
        },
        trailingContent = trailingText?.let {
            {
                Text(
                    text = it,
                    color = trailingColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Text(text = title)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsSwitchItem(
    painter: Painter,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SwitchListItem(
        checked = checked,
        onCheckedChange = onCheckedChange,
        supportingContent = subtitle?.let { { Text(text = it) } },
        leadingContent = {
            Icon(
                painter = painter,
                contentDescription = null,
            )
        },
    ) {
        Text(text = title)
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps
            )
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0D13)
@Composable
private fun Preview() {
    AppTheme {
        SettingsScreenContent(
            onBack = {},
            onNavigateToCredits = {},
            onNavigateToFaq = {},
            themeMode = ThemeMode.DARK,
            glowEnabled = false,
            glowIntensity = 0f,
            extendOnShake = false,
            extendOnShakeMinutes = 15,
            isDeviceAdminEnabled = false,
            hasNotificationAccess = false,
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() },
            purchaseEvents = emptyFlow(),
            productUiModels = emptyList()
        )
    }
}
