package dr.achim.sleep_timer.presentation.settings

import android.app.StatusBarManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.BuildConfig
import dr.achim.sleep_timer.LocalIsPro
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.Constants.EXTEND_ON_SHAKE_STEPS
import dr.achim.sleep_timer.common.Constants.LIGHTS_OFF_DELAY_STEPS
import dr.achim.sleep_timer.common.findActivity
import dr.achim.sleep_timer.model.Product
import dr.achim.sleep_timer.model.PurchaseEvent
import dr.achim.sleep_timer.model.ThemeMode
import dr.achim.sleep_timer.navigation.LocalPaywallController
import dr.achim.sleep_timer.receiver.SleepTimerAdminReceiver
import dr.achim.sleep_timer.service.FeatureFlag
import dr.achim.sleep_timer.service.TimerTileService
import dr.achim.sleep_timer.ui.components.DiagonalRibbon
import dr.achim.sleep_timer.ui.components.SectionTitle
import dr.achim.sleep_timer.ui.components.SwitchListItem
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.GreenAccent
import dr.achim.sleep_timer.ui.theme.dimens
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlinx.coroutines.delay
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
    highlight: String? = null,
    viewModel: SettingsViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val glowEnabled by viewModel.glowEffectEnabled.collectAsStateWithLifecycle()
    val glowIntensity by viewModel.glowIntensity.collectAsStateWithLifecycle()
    val extendOnShake by viewModel.extendOnShake.collectAsStateWithLifecycle()
    val extendOnShakeMinutes by viewModel.extendOnShakeMinutes.collectAsStateWithLifecycle()
    val lightsOffDelay by viewModel.lightsOffDelay.collectAsStateWithLifecycle()
    val lightsOffDelaySeconds by viewModel.lightsOffDelaySeconds.collectAsStateWithLifecycle()
    val isDeviceAdminEnabled by viewModel.isDeviceAdminEnabled.collectAsStateWithLifecycle()
    val hasNotificationAccess by viewModel.hasNotificationAccess.collectAsStateWithLifecycle()
    val productUiModels by viewModel.productUiModels.collectAsStateWithLifecycle()
    val isPrivacyOptionsRequired = viewModel.isPrivacyOptionsRequired

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
        lightsOffDelay = lightsOffDelay,
        lightsOffDelaySeconds = lightsOffDelaySeconds,
        isDeviceAdminEnabled = isDeviceAdminEnabled,
        hasNotificationAccess = hasNotificationAccess,
        onAction = viewModel::onAction,
        highlight = highlight,
        snackbarHostState = snackbarHostState,
        purchaseEvents = viewModel.events,
        productUiModels = productUiModels,
        isPrivacyOptionsRequired = isPrivacyOptionsRequired,
    )
}

const val SETTING_ADMIN = "admin"
const val SETTING_DND = "dnd"

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
    lightsOffDelay: Boolean,
    lightsOffDelaySeconds: Int,
    isDeviceAdminEnabled: Boolean,
    hasNotificationAccess: Boolean,
    onAction: (SettingsUiAction) -> Unit,
    highlight: String?,
    snackbarHostState: SnackbarHostState,
    purchaseEvents: Flow<PurchaseEvent>,
    productUiModels: List<StoreProductUiModel>,
    isPrivacyOptionsRequired: Boolean
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(highlight) {
        if (highlight != null) {
            delay(500.milliseconds)
            scrollState.animateScrollTo(
                value = scrollState.maxValue,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )
        }
    }

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

    val isPro = LocalIsPro.current
    val paywallController = LocalPaywallController.current
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
    // snackbar messages are being handled by viewModel
    LaunchedEffect(Unit) {
        purchaseEvents.collectLatest {
            when (it) {
                PurchaseEvent.PurchaseComplete,
                PurchaseEvent.RestoreSuccess -> {
                    showConfetti = true
                }

                PurchaseEvent.PurchaseAborted,
                PurchaseEvent.PurchaseError,
                PurchaseEvent.RestoreError -> {}
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text(stringResource(R.string.settings_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.settings_back_description)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
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
                        subtitle = stringResource(themeMode.displayName),
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
                        val currentIndex = EXTEND_ON_SHAKE_STEPS
                            .indexOf(extendOnShakeMinutes)
                            .coerceAtLeast(0)
                        SettingsSliderItem(
                            title = pluralStringResource(
                                R.plurals.settings_extend_on_shake_minutes_title,
                                extendOnShakeMinutes,
                                extendOnShakeMinutes
                            ),
                            value = currentIndex.toFloat(),
                            onValueChange = { index ->
                                val minutes = EXTEND_ON_SHAKE_STEPS[index.toInt()]
                                onAction(SettingsUiAction.SetExtendOnShakeMinutes(minutes))
                            },
                            valueRange = 0f..(EXTEND_ON_SHAKE_STEPS.size - 1).toFloat(),
                            steps = EXTEND_ON_SHAKE_STEPS.size - 2
                        )
                    }

                    if (FeatureFlag.DelayLightsOff.enabled) {
                        SettingsSwitchItem(
                            painter = painterResource(R.drawable.ic_more_time),
                            title = stringResource(R.string.settings_lights_off_delay_title),
                            subtitle = stringResource(R.string.settings_lights_off_delay_subtitle),
                            checked = lightsOffDelay,
                            isProFeature = true,
                            onCheckedChange = {
                                if (!isPro) {
                                    paywallController.show()
                                } else {
                                    onAction(SettingsUiAction.SetLightsOffDelay(it))
                                }
                            }
                        )

                        AnimatedVisibility(lightsOffDelay) {
                            val currentDelayIndex = LIGHTS_OFF_DELAY_STEPS
                                .indexOf(lightsOffDelaySeconds)
                                .coerceAtLeast(0)
                            SettingsSliderItem(
                                title = pluralStringResource(
                                    R.plurals.settings_lights_off_delay_seconds_title,
                                    lightsOffDelaySeconds,
                                    lightsOffDelaySeconds
                                ),
                                value = currentDelayIndex.toFloat(),
                                onValueChange = { index ->
                                    if (!isPro) {
                                        paywallController.show()
                                    } else {
                                        val seconds = LIGHTS_OFF_DELAY_STEPS[index.toInt()]
                                        onAction(SettingsUiAction.SetLightsOffDelaySeconds(seconds))
                                    }
                                },
                                isProFeature = true,
                                valueRange = 0f..(LIGHTS_OFF_DELAY_STEPS.size - 1).toFloat(),
                                steps = LIGHTS_OFF_DELAY_STEPS.size - 2
                            )
                        }
                    }

                    if (FeatureFlag.QuickSettingsTile.enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val tileLabel = stringResource(R.string.tile_label)
                        SettingsItem(
                            painter = painterResource(R.drawable.ic_moon_stars),
                            title = stringResource(R.string.settings_tile_request_title),
                            subtitle = stringResource(R.string.settings_tile_request_subtitle),
                            isProFeature = true,
                            onClick = {
                                if (!isPro) {
                                    paywallController.show()
                                } else {
                                    val statusBarManager =
                                        context.getSystemService(StatusBarManager::class.java)
                                    statusBarManager?.requestAddTileService(
                                        ComponentName(context, TimerTileService::class.java),
                                        tileLabel,
                                        Icon.createWithResource(context, R.drawable.ic_moon_stars),
                                        context.mainExecutor
                                    ) { }
                                }
                            }
                        )
                    }
                }

                SettingsSection(
                    modifier = Modifier.animateContentSize(),
                    title = { SectionTitle(stringResource(R.string.settings_section_support_me)) }
                ) {
                    SettingsItem(
                        painter = painterResource(R.drawable.ic_rate_review),
                        title = stringResource(R.string.settings_like_app_title),
                        subtitle = stringResource(R.string.settings_like_app_subtitle),
                        trailingText = stringResource(R.string.settings_like_app_trailing),
                        trailingColor = GreenAccent,
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
                        val icon = when (uiModel.id) {
                            Product.Donation.id -> R.drawable.ic_coffee
                            Product.RemoveAds.id -> R.drawable.ic_ad_off
                            else -> null
                        }

                        icon?.let {
                            SettingsItem(
                                painter = painterResource(icon),
                                title = uiModel.title,
                                subtitle = uiModel.description,
                                trailingText = if (uiModel.isPurchased) stringResource(R.string.settings_already_purchased) else uiModel.price,
                                trailingColor = if (uiModel.isPurchased) GreenAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                onClick = {
                                    if (!uiModel.isPurchased) {
                                        onAction(
                                            SettingsUiAction.PurchaseProduct(
                                                context.findActivity(),
                                                uiModel.id
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }

                    SettingsItem(
                        painter = painterResource(R.drawable.ic_restore),
                        title = stringResource(R.string.settings_restore_purchases),
                        onClick = { onAction(SettingsUiAction.RestorePurchases) }
                    )
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
                        highlighted = highlight == SETTING_ADMIN,
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
                        highlighted = highlight == SETTING_DND,
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
                    if (isPrivacyOptionsRequired) {
                        SettingsItem(
                            painter = painterResource(R.drawable.ic_shield_person),
                            title = stringResource(R.string.settings_privacy_options_title),
                            onClick = { onAction(SettingsUiAction.ShowPrivacyOptions(context.findActivity())) }
                        )
                    }
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
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
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
            shape = MaterialTheme.shapes.extraLarge
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
                    .filter { Build.VERSION.SDK_INT >= it.minSdk }
                    .forEach { mode ->
                        val interactionSource = remember { MutableInteractionSource() }

                        SegmentedListItem(
                            selected = currentThemeMode == mode,
                            onClick = { onThemeModeSelected(mode) },
                            shapes = ListItemDefaults.shapes(),
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = {
                                RadioButton(
                                    selected = currentThemeMode == mode,
                                    onClick = null,
                                    interactionSource = interactionSource
                                )
                            },
                            interactionSource = interactionSource
                        ) {
                            Text(text = stringResource(mode.displayName))
                        }
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

@Composable
fun SettingsItem(
    painter: Painter,
    title: String,
    subtitle: String? = null,
    isProFeature: Boolean = false,
    enabled: Boolean = true,
    trailingText: String? = null,
    trailingColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    val isPro = LocalIsPro.current
    Box(modifier = Modifier.clipToBounds()) {
        ListItem(
            onClick = onClick,
            enabled = enabled,
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
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Text(text = title)
        }

        if (isProFeature && !isPro) {
            DiagonalRibbon { Text(stringResource(R.string.common_pro)) }
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    painter: Painter,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    highlighted: Boolean = false,
    isProFeature: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    val isPro = LocalIsPro.current
    Box(modifier = Modifier.clipToBounds()) {
        SwitchListItem(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            highlighted = highlighted,
            supportingContent = subtitle?.let { { Text(text = it) } },
            leadingContent = {
                Icon(
                    painter = painter,
                    contentDescription = null,
                )
            }
        ) {
            Text(text = title)
        }

        if (isProFeature && !isPro) {
            DiagonalRibbon { Text(stringResource(R.string.common_pro)) }
        }
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    isProFeature: Boolean = false,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    val isPro = LocalIsPro.current
    Box(modifier = Modifier.clipToBounds()) {
        ListItem(
            enabled = enabled,
            supportingContent = {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    valueRange = valueRange,
                    steps = steps
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Text(
                text = title,
                color = if (enabled) Color.Unspecified else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }

        if (isProFeature && !isPro) {
            DiagonalRibbon { Text(stringResource(R.string.common_pro)) }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0D13)
@Composable
private fun Preview() {
    AppTheme {
        SettingsScreenContent(
            onBack = {},
            onNavigateToCredits = {},
            onNavigateToFaq = {},
            themeMode = ThemeMode.DarkOrange,
            glowEnabled = false,
            glowIntensity = 0f,
            extendOnShake = false,
            extendOnShakeMinutes = 15,
            lightsOffDelay = false,
            lightsOffDelaySeconds = 0,
            isDeviceAdminEnabled = false,
            hasNotificationAccess = false,
            onAction = {},
            highlight = null,
            snackbarHostState = remember { SnackbarHostState() },
            purchaseEvents = emptyFlow(),
            productUiModels = emptyList(),
            isPrivacyOptionsRequired = false
        )
    }
}
