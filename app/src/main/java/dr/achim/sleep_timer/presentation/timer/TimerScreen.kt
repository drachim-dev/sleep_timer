package dr.achim.sleep_timer.presentation.timer

import android.content.Intent
import android.media.AudioManager
import android.os.Build
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.ReviewManager
import dr.achim.sleep_timer.common.findActivity
import dr.achim.sleep_timer.domain.model.AppCategory
import dr.achim.sleep_timer.domain.model.QuickLaunchApp
import dr.achim.sleep_timer.model.HueActionSource
import dr.achim.sleep_timer.model.TimerActions
import dr.achim.sleep_timer.model.TimerState
import dr.achim.sleep_timer.presentation.settings.SETTING_ADMIN
import dr.achim.sleep_timer.presentation.settings.SETTING_DND
import dr.achim.sleep_timer.ui.SharedElementKey
import dr.achim.sleep_timer.ui.components.CircularTimer
import dr.achim.sleep_timer.ui.components.QuickLaunchAppItem
import dr.achim.sleep_timer.ui.components.QuickLaunchItem
import dr.achim.sleep_timer.ui.components.QuickLaunchPlaceholder
import dr.achim.sleep_timer.ui.components.SectionTitle
import dr.achim.sleep_timer.ui.components.TimeButton
import dr.achim.sleep_timer.ui.safeSharedElement
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.OrangeAccent
import dr.achim.sleep_timer.ui.theme.RedAccent
import dr.achim.sleep_timer.ui.theme.dimens
import org.koin.compose.koinInject
import dr.achim.sleep_timer.presentation.timer.TimerUiAction as Action

@Composable
fun TimerScreen(
    onBack: () -> Unit,
    onNavigateToRoomSelection: (HueActionSource) -> Unit,
    onNavigateToSettings: (String) -> Unit,
    viewModel: TimerViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val reviewManager = koinInject<ReviewManager>()

    LifecycleResumeEffect(Unit) {
        viewModel.onAction(Action.RefreshPermissions)
        viewModel.onAction(Action.OnResume)
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is TimerUiEvent.NavigateToRoomSelection -> onNavigateToRoomSelection(event.source)
                TimerUiEvent.RequestReview -> reviewManager.tryShowReview(context.findActivity())
            }
        }
    }

    TimerScreenContent(
        onBack = onBack,
        onAction = viewModel::onAction,
        onNavigateToSettings = onNavigateToSettings,
        uiState = uiState,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerScreenContent(
    onBack: () -> Unit,
    onAction: (Action) -> Unit,
    onNavigateToSettings: (String) -> Unit,
    uiState: TimerUiState,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showQuickLaunchSheet by remember { mutableStateOf(false) }
    var selectingIndex by remember { mutableIntStateOf(-1) }

    var showStartVolumeDialog by remember { mutableStateOf(false) }
    var showEndVolumeDialog by remember { mutableStateOf(false) }

    if (showStartVolumeDialog) {
        VolumeSliderDialog(
            initialValue = uiState.timerActions.startActions.volumeLevel,
            onConfirm = { level ->
                onAction(Action.SetStartVolumeLevel(level))
                onAction(Action.ToggleStartVolume(true))
                showStartVolumeDialog = false
            },
            onValueChange = { level ->
                onAction(Action.SetMediaVolume(level, AudioManager.FLAG_SHOW_UI))
            },
            onDismiss = { showStartVolumeDialog = false }
        )
    }

    if (showEndVolumeDialog) {
        VolumeSliderDialog(
            initialValue = uiState.timerActions.endActions.volumeLevel,
            onConfirm = { level ->
                onAction(Action.SetEndVolumeLevel(level))
                onAction(Action.ToggleEndVolume(true))
                showEndVolumeDialog = false
            },
            onValueChange = { level ->
                onAction(Action.SetMediaVolume(level, AudioManager.FLAG_SHOW_UI))
            },
            onDismiss = { showEndVolumeDialog = false }
        )
    }

    if (showQuickLaunchSheet) {
        QuickLaunchBottomSheet(
            title = if (selectingIndex != -1) stringResource(R.string.timer_pin_app_title) else stringResource(
                R.string.timer_quick_launch_title
            ),
            apps = uiState.quickLaunchApps,
            selectingIndex = selectingIndex,
            onAppClick = { packageName ->
                if (selectingIndex != -1) {
                    onAction(Action.SetQuickLaunchApp(selectingIndex, packageName))
                } else {
                    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                    intent?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(it)
                    }
                }
                showQuickLaunchSheet = false
                selectingIndex = -1
            },
            onDismiss = {
                showQuickLaunchSheet = false
                selectingIndex = -1
            }
        )
    }

    val isRunning = uiState.timerState is TimerState.Running
    val fabColor by animateColorAsState(
        targetValue = if (isRunning) RedAccent else OrangeAccent,
        label = "fabColor"
    )
    var fabHeight by remember { mutableIntStateOf(0) }
    val fabHeightDp = with(LocalDensity.current) { fabHeight.toDp() + 16.dp }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.settings_back_description)
                        )
                    }
                },
                actions = {
                    val progress by LocalNavAnimatedContentScope.current.transition.animateFloat(
                        transitionSpec = {
                            spring(
                                dampingRatio = Spring.DampingRatioHighBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        }
                    ) { state ->
                        if (state == EnterExitState.Visible) 1f else 0f
                    }
                    IconButton(
                        onClick = {
                            onAction(Action.StopTimer)
                            onBack()
                        },
                        modifier = Modifier.safeSharedElement(SharedElementKey.ActionButtonGearToCross)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.timer_close_description),
                            modifier = Modifier.graphicsLayer {
                                alpha = progress
                                rotationZ = progress * 90f
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    when (uiState.timerState) {
                        is TimerState.Idle -> {
                            val millis = if (uiState.timerState.remainingTimeMillis > 0)
                                uiState.timerState.remainingTimeMillis
                            else 20 * 60 * 1000L
                            onAction(Action.StartTimer(millis))
                        }

                        is TimerState.Running,
                        is TimerState.Paused -> {
                            onAction(Action.TogglePauseResume)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .onGloballyPositioned { fabHeight = it.size.height }
                    .safeSharedElement(SharedElementKey.Fab),
                shape = MaterialTheme.shapes.extraLarge,
                containerColor = fabColor,
                contentColor = Color.White
            ) {
                Icon(
                    painter = rememberAnimatedVectorPainter(
                        AnimatedImageVector.animatedVectorResource(R.drawable.avd_play_to_pause),
                        isRunning
                    ),
                    contentDescription = null
                )
                Spacer(Modifier.width(AppTheme.dimens.spacingNormal))
                Text(
                    text = when (uiState.timerState) {
                        is TimerState.Idle -> stringResource(R.string.timer_start)
                        is TimerState.Paused -> stringResource(R.string.timer_resume)
                        is TimerState.Running -> stringResource(R.string.timer_pause)
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .systemBarsPadding()
                .displayCutoutPadding()
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = AppTheme.dimens.spacingMedium)
                .padding(bottom = AppTheme.dimens.spacingLarge + fabHeightDp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularTimer(
                progress = uiState.timerState.progress,
                glowEnabled = uiState.glowEnabled,
                glowIntensity = uiState.glowIntensity,
                interactive = false,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .safeSharedElement(SharedElementKey.CircularTimer)
                    .let {
                        val expandedSize = AppTheme.dimens.timerSizeExpanded
                        val collapsedSize = AppTheme.dimens.timerSizeCollapsed
                        it.layout { measurable, _ ->
                            val fraction = (scrollState.value / 400f).coerceIn(0f, 1f)
                            val sizeDp = lerp(expandedSize, collapsedSize, fraction)
                            val sizePx = sizeDp.roundToPx()

                            val placeable =
                                measurable.measure(Constraints.fixed(sizePx, sizePx))
                            layout(sizePx, sizePx) {
                                placeable.placeRelative(0, 0)
                            }
                        }
                    },
                onProgressChange = { newProgress ->
                    val totalMillis = (newProgress * 60 * 60 * 1000).toLong()
                    onAction(Action.SetRemainingTime(totalMillis))
                }
            ) {
                Text(
                    text = uiState.timerState.formattedTime,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = LocalTextStyle.current.fontSize)
                )
            }

            TimeAdjustmentRow(
                modifier = Modifier.fillMaxWidth(),
                times = listOf(1, 5, 20),
                onClick = { onAction(Action.AddMinutes(it.toLong())) }
            )

            TimerSection(
                title = { SectionTitle(stringResource(R.string.timer_quick_launch_title)) }
            ) {
                QuickLaunchRow(
                    selectedApps = uiState.selectedApps,
                    onPinApp = { index ->
                        selectingIndex = index
                        showQuickLaunchSheet = true
                    },
                    onShowAll = {
                        selectingIndex = -1
                        showQuickLaunchSheet = true
                    }
                )
            }

            TimerSection(
                title = { SectionTitle(stringResource(R.string.timer_section_start_actions)) }
            ) {
                StartActionsRow(
                    timerActions = uiState.timerActions,
                    hasDndPermission = uiState.hasNotificationAccess,
                    hasNearbyPermission = uiState.hasNearbyPermission,
                    onAction = onAction,
                    onVolumeLongClick = { showStartVolumeDialog = true },
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            TimerSection(
                title = { SectionTitle(stringResource(R.string.timer_section_end_actions)) }
            ) {
                EndActionsRow(
                    timerActions = uiState.timerActions,
                    isDeviceAdminEnabled = uiState.isDeviceAdminEnabled,
                    onAction = onAction,
                    onVolumeLongClick = { showEndVolumeDialog = true },
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
private fun StartActionsRow(
    timerActions: TimerActions,
    hasDndPermission: Boolean,
    hasNearbyPermission: Boolean,
    onAction: (Action) -> Unit,
    onVolumeLongClick: () -> Unit,
    onNavigateToSettings: (String) -> Unit
) {
    ActionToggle(
        painter = painterResource(if (timerActions.startActions.volumeLevel == 0) R.drawable.ic_volume_mute else R.drawable.ic_volume_down),
        label = timerActions.startActions.volumeLevel?.let { "$it %" }
            ?: stringResource(R.string.timer_action_volume),
        active = timerActions.startActions.adjustVolume,
        onClick = {
            if (timerActions.startActions.volumeLevel == null && !timerActions.startActions.adjustVolume) {
                onVolumeLongClick()
            } else {
                onAction(Action.ToggleStartVolume(!timerActions.startActions.adjustVolume))
            }
        },
        onLongClick = onVolumeLongClick
    )
    ActionToggle(
        painter = painterResource(if (timerActions.startActions.hueLights) R.drawable.ic_lights_off else R.drawable.ic_lights_on),
        label = stringResource(R.string.timer_action_hue_lights),
        active = timerActions.startActions.hueLights,
        warning = timerActions.startActions.hueLights && !hasNearbyPermission,
        onClick = {
            if (hasNearbyPermission) {
                onAction(Action.ToggleHueLights(!timerActions.startActions.hueLights))
            } else {
                onAction(Action.OpenHueSettings(HueActionSource.START))
            }
        },
        onLongClick = { onAction(Action.OpenHueSettings(HueActionSource.START)) }
    )
    ActionToggle(
        painter = painterResource(if (timerActions.startActions.enableDnd) R.drawable.ic_dnd_on else R.drawable.ic_dnd_off),
        label = stringResource(R.string.timer_action_dnd),
        active = timerActions.startActions.enableDnd,
        warning = timerActions.startActions.enableDnd && !hasDndPermission,
        onClick = {
            if (hasDndPermission) {
                onAction(Action.ToggleDnd(!timerActions.startActions.enableDnd))
            } else {
                onNavigateToSettings(SETTING_DND)
            }
        }
    )
}

@Composable
private fun EndActionsRow(
    timerActions: TimerActions,
    isDeviceAdminEnabled: Boolean,
    onAction: (Action) -> Unit,
    onVolumeLongClick: () -> Unit,
    onNavigateToSettings: (String) -> Unit
) {
    ActionToggle(
        painter = painterResource(if (timerActions.endActions.stopMedia) R.drawable.ic_media_off else R.drawable.ic_media_on),
        label = stringResource(R.string.timer_action_media),
        active = timerActions.endActions.stopMedia,
        onClick = { onAction(Action.ToggleStopMedia(!timerActions.endActions.stopMedia)) }
    )
    ActionToggle(
        painter = painterResource(if (timerActions.endActions.volumeLevel == 0) R.drawable.ic_volume_mute else R.drawable.ic_volume_down),
        label = timerActions.endActions.volumeLevel?.let { "$it %" }
            ?: stringResource(R.string.timer_action_volume),
        active = timerActions.endActions.adjustVolume,
        onClick = {
            if (timerActions.endActions.volumeLevel == null && !timerActions.endActions.adjustVolume) {
                onVolumeLongClick()
            } else {
                onAction(Action.ToggleEndVolume(!timerActions.endActions.adjustVolume))
            }
        },
        onLongClick = onVolumeLongClick
    )
    ActionToggle(
        painter = painterResource(if (timerActions.endActions.turnOffScreen) R.drawable.ic_screen_off else R.drawable.ic_screen_on),
        label = stringResource(R.string.timer_action_screen),
        active = timerActions.endActions.turnOffScreen,
        warning = timerActions.endActions.turnOffScreen && !isDeviceAdminEnabled,
        onClick = {
            if (isDeviceAdminEnabled) {
                onAction(Action.ToggleScreenOff(!timerActions.endActions.turnOffScreen))
            } else {
                onNavigateToSettings(SETTING_ADMIN)
            }
        }
    )
    // Implement when dimming lights is available
    /* ActionToggle(
        painter = painterResource(if (timerActions.endActions.hueLights) R.drawable.ic_lights_off else R.drawable.ic_lights_on),
        label = stringResource(R.string.timer_action_hue_lights),
        active = timerActions.endActions.hueLights,
        onClick = { onAction(Action.ToggleEndHueLights(!timerActions.endActions.hueLights)) },
        onLongClick = { onAction(Action.OpenHueSettings(HueActionSource.END)) }
    ) */
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        ActionToggle(
            painter = painterResource(if (timerActions.endActions.turnOffBluetooth) R.drawable.ic_bluetooth_off else R.drawable.ic_bluetooth_on),
            label = stringResource(R.string.timer_action_bluetooth),
            active = timerActions.endActions.turnOffBluetooth,
            onClick = { onAction(Action.ToggleBluetooth(!timerActions.endActions.turnOffBluetooth)) }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0D13)
@Composable
fun TimerScreenPreview() {
    AppTheme {
        TimerScreenContent(
            onBack = {},
            onAction = {},
            onNavigateToSettings = {},
            uiState = TimerUiState(),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Composable
fun TimeAdjustmentRow(
    modifier: Modifier = Modifier,
    times: List<Int>,
    onClick: (time: Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        times.forEach {
            TimeButton(onClick = { onClick(it) }) {
                Text(text = "+$it")
            }
        }
    }
}

@Composable
private fun TimerSection(title: @Composable () -> Unit, content: @Composable RowScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        title()
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingNormal),
            content = content
        )
    }
}

@Composable
private fun QuickLaunchRow(
    selectedApps: List<QuickLaunchApp?>,
    onPinApp: (index: Int) -> Unit,
    onShowAll: () -> Unit
) {
    val context = LocalContext.current
    selectedApps.forEachIndexed { index, app ->
        if (app != null) {
            QuickLaunchAppItem(
                app = app,
                onClick = {
                    val intent =
                        context.packageManager.getLaunchIntentForPackage(app.packageName)
                    intent?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(it)
                    }
                },
                onLongClick = { onPinApp(index) }
            )
        } else {
            QuickLaunchPlaceholder(
                onClick = { onPinApp(index) }
            )
        }
    }

    QuickLaunchItem(
        onClick = onShowAll,
        label = stringResource(R.string.timer_apps),
        icon = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_apps),
                        contentDescription = stringResource(R.string.timer_apps),
                        modifier = Modifier.size(AppTheme.dimens.quickLaunchIconSize),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLaunchBottomSheet(
    title: String,
    apps: List<QuickLaunchApp>,
    selectingIndex: Int,
    onAppClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val groupedApps = remember(apps) { apps.groupBy { it.category } }
    var selectedAppPackageName by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Expanded,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppTheme.dimens.spacingMedium),
            textAlign = TextAlign.Center
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(AppTheme.dimens.spacingMedium),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingNormal)
        ) {
            groupedApps.forEach { (category, appsInCategory) ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = when (category) {
                            AppCategory.MEDIA -> stringResource(R.string.timer_category_media)
                            AppCategory.ALARM -> stringResource(R.string.timer_category_alarm)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                items(appsInCategory) { app ->
                    QuickLaunchAppItem(
                        app = app,
                        onClick = { onAppClick(app.packageName) },
                        onLongClick = {
                            if (selectingIndex == -1) {
                                selectedAppPackageName = app.packageName
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionToggle(
    painter: Painter,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    warning: Boolean = false
) {
    val containerColor by animateColorAsState(
        when {
            warning -> OrangeAccent.copy(alpha = 0.2f)
            active -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            else -> Color.Transparent
        }
    )

    val contentColor by animateColorAsState(
        when {
            warning -> OrangeAccent
            active -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
        }
    )

    Surface(
        modifier = Modifier
            .size(AppTheme.dimens.quickLaunchCardWidth, AppTheme.dimens.quickLaunchItemHeight)
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button
            ),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = if (warning) painterResource(R.drawable.ic_warning) else painter,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(AppTheme.dimens.actionToggleIconSize)
            )

            Spacer(Modifier.height(AppTheme.dimens.spacingSmall))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VolumeSliderDialog(
    initialValue: Int?,
    onConfirm: (Int) -> Unit,
    onValueChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf((initialValue ?: 50).toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.timer_volume_dialog_title)) },
        text = {
            Column {
                Text(
                    text = "${sliderValue.toInt()} %",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(AppTheme.dimens.spacingMedium))
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onValueChange(it.toInt())
                    },
                    valueRange = 0f..100f
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(sliderValue.toInt()) }) {
                Text(stringResource(R.string.timer_volume_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.timer_volume_cancel))
            }
        }
    )
}
