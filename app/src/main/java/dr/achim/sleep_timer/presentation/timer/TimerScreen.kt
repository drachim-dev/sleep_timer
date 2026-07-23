package dr.achim.sleep_timer.presentation.timer

import android.content.Intent
import android.media.AudioManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexBoxScope
import androidx.compose.foundation.layout.FlexJustifyContent
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.ReviewManager
import dr.achim.sleep_timer.domain.model.AppCategory
import dr.achim.sleep_timer.domain.model.QuickLaunchApp
import dr.achim.sleep_timer.model.TimerActionSource
import dr.achim.sleep_timer.model.TimerActionType
import dr.achim.sleep_timer.model.TimerActions
import dr.achim.sleep_timer.model.TimerState
import dr.achim.sleep_timer.presentation.settings.SETTING_ADMIN
import dr.achim.sleep_timer.presentation.settings.SETTING_DND
import dr.achim.sleep_timer.ui.SharedElementKey
import dr.achim.sleep_timer.ui.components.CircularTimer
import dr.achim.sleep_timer.ui.components.CollapsingScaffold
import dr.achim.sleep_timer.ui.components.QuickLaunchAppItem
import dr.achim.sleep_timer.ui.components.QuickLaunchItem
import dr.achim.sleep_timer.ui.components.QuickLaunchPlaceholder
import dr.achim.sleep_timer.ui.components.SectionTitle
import dr.achim.sleep_timer.ui.components.TimeButton
import dr.achim.sleep_timer.ui.components.rememberCollapsingHeaderState
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
    onNavigateToRoomSelection: (TimerActionSource) -> Unit,
    onNavigateToSettings: (String) -> Unit,
    viewModel: TimerViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
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
                TimerUiEvent.RequestReview -> reviewManager.tryShowReview(context)
            }
        }
    }

    TimerScreenContent(
        onBack = onBack,
        onAction = viewModel::onAction,
        onNavigateToSettings = onNavigateToSettings,
        uiState = uiState,
        timerState = timerState,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalFlexBoxApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TimerScreenContent(
    onBack: () -> Unit,
    onAction: (Action) -> Unit,
    onNavigateToSettings: (String) -> Unit,
    uiState: TimerUiState,
    timerState: TimerState,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val expandedTimerSize = AppTheme.dimens.timerSizeExpanded

    var showQuickLaunchSheet by remember { mutableStateOf(false) }
    var selectingIndex by remember { mutableIntStateOf(-1) }

    var showStartVolumeDialog by remember { mutableStateOf(false) }
    var showEndVolumeDialog by remember { mutableStateOf(false) }

    if (showStartVolumeDialog) {
        VolumeSliderDialog(
            initialValue = uiState.timerActions.startActions.volumeLevel,
            onConfirm = { level ->
                onAction(Action.SetVolumeLevel(TimerActionSource.START, level))
                onAction(Action.ToggleAction(TimerActionType.ADJUST_VOLUME, TimerActionSource.START, true))
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
                onAction(Action.SetVolumeLevel(TimerActionSource.END, level))
                onAction(Action.ToggleAction(TimerActionType.ADJUST_VOLUME, TimerActionSource.END, true))
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

    val isRunning = timerState is TimerState.Running
    val fabColor by animateColorAsState(
        targetValue = if (isRunning) RedAccent else OrangeAccent,
        label = "fabColor"
    )
    var fabHeight by remember { mutableIntStateOf(0) }
    val fabHeightDp = with(LocalDensity.current) { fabHeight.toDp() + 16.dp }
    val listState = rememberLazyListState()

    CollapsingScaffold(
        modifier = Modifier.fillMaxSize(),
        state = rememberCollapsingHeaderState(maxHeaderHeight = expandedTimerSize, listState = listState),
        topBar = { isCollapsed ->
            CenterAlignedTopAppBar(
                title = {
                    AnimatedVisibility(visible = isCollapsed) {
                        Text(
                            text = timerState.formattedTime,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.safeSharedElement(SharedElementKey.TimerText, animatedVisibilityScope = this),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.settings_back_description)
                        )
                    }
                },
                actions = {
                    val progress = if (LocalInspectionMode.current) {
                        1f
                    } else {
                        LocalNavAnimatedContentScope.current.transition.animateFloat(
                            transitionSpec = {
                                spring(
                                    dampingRatio = Spring.DampingRatioHighBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            }
                        ) { state ->
                            if (state == EnterExitState.Visible) 1f else 0f
                        }.value
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(
                        alpha = if (isCollapsed) 1f else 0f
                    )
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    when (timerState) {
                        is TimerState.Idle -> {
                            val millis = if (timerState.remainingTimeMillis > 0)
                                timerState.remainingTimeMillis
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
                    text = when (timerState) {
                        is TimerState.Idle -> stringResource(R.string.timer_start)
                        is TimerState.Paused -> stringResource(R.string.timer_resume)
                        is TimerState.Running -> stringResource(R.string.timer_pause)
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        collapsingHeader = { _, isCollapsed ->
            CircularTimer(
                progress = timerState.progress,
                glowEnabled = uiState.glowEnabled,
                glowIntensity = uiState.glowIntensity,
                interactive = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .size(expandedTimerSize),
                onProgressChange = { newProgress ->
                    val totalMillis = (newProgress * 60 * 60 * 1000).toLong()
                    onAction(Action.SetRemainingTime(totalMillis))
                }
            ) {
                AnimatedVisibility(!isCollapsed) {
                    Text(
                        text = timerState.formattedTime,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.displayLarge.fontSize),
                        style = LocalTextStyle.current,
                        modifier = Modifier.safeSharedElement(SharedElementKey.TimerText, animatedVisibilityScope = this),
                    )
                }
            }
        },

    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = AppTheme.dimens.spacingLarge + fabHeightDp,
                top = AppTheme.dimens.spacingNormal
            ) + innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingNormal),
        ) {
        stickyHeader {
            TimeAdjustmentRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = AppTheme.dimens.spacingSmall)
                    .padding(horizontal = AppTheme.dimens.spacingMedium),
                times = listOf(1, 5, 20),
                onClick = { onAction(Action.AddMinutes(it.toLong())) }
            )
        }

        item {
            TimerSection(
                title = { SectionTitle(stringResource(R.string.timer_quick_launch_title)) },
                modifier = Modifier.padding(horizontal = AppTheme.dimens.spacingMedium)
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
        }

        item {
            TimerSection(
                title = { SectionTitle(stringResource(R.string.timer_section_start_actions)) },
                modifier = Modifier.padding(horizontal = AppTheme.dimens.spacingMedium)
            ) {
                StartActionsRow(
                    timerActions = uiState.timerActions,
                    hasDndPermission = uiState.hasNotificationAccess,
                    hasNearbyPermission = uiState.hasNearbyPermission,
                    isDeviceAdminEnabled = uiState.isDeviceAdminEnabled,
                    onAction = onAction,
                    onVolumeLongClick = { showStartVolumeDialog = true },
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }

        item {
            TimerSection(
                title = { SectionTitle(stringResource(R.string.timer_section_end_actions)) },
                modifier = Modifier.padding(horizontal = AppTheme.dimens.spacingMedium)
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
}

@Composable
private fun StartActionsRow(
    timerActions: TimerActions,
    hasDndPermission: Boolean,
    hasNearbyPermission: Boolean,
    isDeviceAdminEnabled: Boolean,
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
                onAction(
                    Action.ToggleAction(
                        TimerActionType.ADJUST_VOLUME,
                        TimerActionSource.START,
                        !timerActions.startActions.adjustVolume
                    )
                )
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
            if (hasNearbyPermission || timerActions.startActions.hueLights) {
                onAction(
                    Action.ToggleAction(
                        TimerActionType.HUE_LIGHTS,
                        TimerActionSource.START,
                        !timerActions.startActions.hueLights
                    )
                )
            } else {
                onAction(Action.OpenHueSettings(TimerActionSource.START))
            }
        },
        onLongClick = { onAction(Action.OpenHueSettings(TimerActionSource.START)) }
    )
    ActionToggle(
        painter = painterResource(if (timerActions.startActions.enableDnd) R.drawable.ic_dnd_on else R.drawable.ic_dnd_off),
        label = stringResource(R.string.timer_action_dnd),
        active = timerActions.startActions.enableDnd,
        warning = timerActions.startActions.enableDnd && !hasDndPermission,
        onClick = {
            if (hasDndPermission || timerActions.startActions.enableDnd) {
                onAction(
                    Action.ToggleAction(
                        TimerActionType.DND,
                        TimerActionSource.START,
                        !timerActions.startActions.enableDnd
                    )
                )
            } else {
                onNavigateToSettings(SETTING_DND)
            }
        },
        onLongClick = { onNavigateToSettings(SETTING_DND) }
    )
    ActionToggle(
        painter = painterResource(if (timerActions.startActions.turnOffScreen) R.drawable.ic_screen_off else R.drawable.ic_screen_on),
        label = stringResource(R.string.timer_action_screen),
        active = timerActions.startActions.turnOffScreen,
        warning = timerActions.startActions.turnOffScreen && !isDeviceAdminEnabled,
        onClick = {
            if (isDeviceAdminEnabled || timerActions.startActions.turnOffScreen) {
                onAction(
                    Action.ToggleAction(
                        TimerActionType.TURN_OFF_SCREEN,
                        TimerActionSource.START,
                        !timerActions.startActions.turnOffScreen
                    )
                )
            } else {
                onNavigateToSettings(SETTING_ADMIN)
            }
        },
        onLongClick = { onNavigateToSettings(SETTING_ADMIN) }
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
        onClick = {
            onAction(
                Action.ToggleAction(
                    TimerActionType.STOP_MEDIA,
                    TimerActionSource.END,
                    !timerActions.endActions.stopMedia
                )
            )
        }
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
                onAction(
                    Action.ToggleAction(
                        TimerActionType.ADJUST_VOLUME,
                        TimerActionSource.END,
                        !timerActions.endActions.adjustVolume
                    )
                )
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
            if (isDeviceAdminEnabled || timerActions.endActions.turnOffScreen) {
                onAction(
                    Action.ToggleAction(
                        TimerActionType.TURN_OFF_SCREEN,
                        TimerActionSource.END,
                        !timerActions.endActions.turnOffScreen
                    )
                )
            } else {
                onNavigateToSettings(SETTING_ADMIN)
            }
        },
        onLongClick = { onNavigateToSettings(SETTING_ADMIN) }
    )
    // Implement when dimming lights is available
    /* ActionToggle(
        painter = painterResource(if (timerActions.endActions.hueLights) R.drawable.ic_lights_off else R.drawable.ic_lights_on),
        label = stringResource(R.string.timer_action_hue_lights),
        active = timerActions.endActions.hueLights,
        onClick = { onAction(Action.ToggleAction(TimerActionType.HUE_LIGHTS, TimerActionSource.END, !timerActions.endActions.hueLights)) },
        onLongClick = { onAction(Action.OpenHueSettings(TimerActionSource.END)) }
    ) */
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        ActionToggle(
            painter = painterResource(if (timerActions.endActions.turnOffBluetooth) R.drawable.ic_bluetooth_off else R.drawable.ic_bluetooth_on),
            label = stringResource(R.string.timer_action_bluetooth),
            active = timerActions.endActions.turnOffBluetooth,
            onClick = {
                onAction(
                    Action.ToggleAction(
                        TimerActionType.TURN_OFF_BLUETOOTH,
                        TimerActionSource.END,
                        !timerActions.endActions.turnOffBluetooth
                    )
                )
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0D13, heightDp = 500)
@Composable
fun TimerScreenPreview() {
    AppTheme {
        TimerScreenContent(
            onBack = {},
            onAction = {},
            onNavigateToSettings = {},
            uiState = TimerUiState(),
            timerState = TimerState.Idle(),
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

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
private fun TimerSection(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable FlexBoxScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        title()

        val columnMinSpacing = AppTheme.dimens.spacingNormal
        val rowMinSpacing = AppTheme.dimens.spacingMedium
        FlexBox(
            modifier = Modifier.fillMaxWidth(),
            config = {
                wrap(FlexWrap.Wrap)
                columnGap(columnMinSpacing)
                rowGap(rowMinSpacing)
                justifyContent(FlexJustifyContent.SpaceEvenly)
            },
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
            .size(AppTheme.dimens.actionToggleWidth, AppTheme.dimens.actionToggleHeight)
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
