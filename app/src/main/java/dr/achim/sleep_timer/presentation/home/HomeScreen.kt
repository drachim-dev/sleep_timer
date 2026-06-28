package dr.achim.sleep_timer.presentation.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.LocalIsPro
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.findActivity
import dr.achim.sleep_timer.data.AdManager
import dr.achim.sleep_timer.model.TimerState
import dr.achim.sleep_timer.ui.SharedElementKey
import dr.achim.sleep_timer.ui.components.CircularTimer
import dr.achim.sleep_timer.ui.components.DefaultButton
import dr.achim.sleep_timer.ui.components.InitialAnimation
import dr.achim.sleep_timer.ui.components.TimeButton
import dr.achim.sleep_timer.ui.dashedBorder
import dr.achim.sleep_timer.ui.safeSharedElement
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.OrangeAccent
import dr.achim.sleep_timer.ui.theme.RedAccent
import dr.achim.sleep_timer.ui.theme.dimens
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onNavigateToTimer: (Int?) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    adManager: AdManager = koinInject(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current.findActivity()
    val isProUser = LocalIsPro.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isProUser, uiState.timerStartCount) {
        if (!isProUser) {
            adManager.mayPreload()
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToTimer = {
            coroutineScope.launch {
                val selectedMinutes = if (uiState.timerState is TimerState.Idle) uiState.lastSelectedMinutes else null
                if (adManager.shouldShowAd(isProUser)) {
                    adManager.showAd(activity) {
                        onNavigateToTimer(selectedMinutes)
                    }
                } else {
                    onNavigateToTimer(selectedMinutes)
                }
            }
        },
        onNavigateToSettings = onNavigateToSettings,
        onAction = viewModel::onAction,
        onHasNotificationPermission = viewModel::hasNotificationPermission,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToTimer: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onAction: (HomeUiAction) -> Unit,
    onHasNotificationPermission: () -> Boolean,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onNavigateToTimer()
            } else {
                val showRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else {
                    false
                }
                onAction(HomeUiAction.NotificationPermissionDenied(showRationale))
            }
        }
    )

    val coroutineScope = rememberCoroutineScope()
    val selectedMinutes = remember { Animatable(uiState.lastSelectedMinutes / 60f) }
    var showEditSheet by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(uiState.lastSelectedMinutes) {
        if ((selectedMinutes.value * 60).toInt() != uiState.lastSelectedMinutes) {
            selectedMinutes.snapTo(uiState.lastSelectedMinutes / 60f)
        }
    }

    if (showEditSheet) {
        QuickTimeEditSheet(
            initialValue = uiState.quickTimes.getOrNull(editingIndex) ?: -1,
            onDismiss = { showEditSheet = false },
            onSave = { mins ->
                onAction(HomeUiAction.UpdateQuickTime(editingIndex, mins))
                showEditSheet = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(onNavigateToSettings)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            InitialAnimation {
                HomeFab(
                    timerState = uiState.timerState,
                    onNavigateToTimer = {
                        if (onHasNotificationPermission()) {
                            onNavigateToTimer()
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    onStopTimer = { onAction(HomeUiAction.StopTimer) }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isIdle = uiState.timerState is TimerState.Idle
            AnimatedContent(isIdle, label = "timerContent") { isIdle ->
                CircularTimer(
                    progress = if (isIdle) selectedMinutes.value else uiState.timerState.progress,
                    glowEnabled = uiState.glowEnabled,
                    glowIntensity = uiState.glowIntensity,
                    interactive = isIdle,
                    onProgressChange = { newProgress ->
                        if (uiState.timerState !is TimerState.Idle) return@CircularTimer
                        coroutineScope.launch {
                            val snappedProgress = newProgress.coerceAtLeast(1/60f) // at least 1 min
                            selectedMinutes.snapTo(snappedProgress)
                            onAction(HomeUiAction.UpdateLastSelectedMinutes((snappedProgress * 60).toInt()))
                        }
                    },
                    modifier = Modifier.safeSharedElement(SharedElementKey.CircularTimer)
                ) {
                    if (isIdle) {
                        val totalMinutes = (selectedMinutes.value * 60).toInt()
                        val displayMinutes = if (totalMinutes == 0) 0 else ((totalMinutes - 1) % 60) + 1
                        val displayHours = if (totalMinutes == 0) 0 else (totalMinutes - 1) / 60

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.home_time_minutes_only, displayMinutes),
                                maxLines = 1,
                                autoSize = TextAutoSize.StepBased(maxFontSize = LocalTextStyle.current.fontSize)
                            )
                            if (displayHours > 0) {
                                Text(
                                    text = "+ ${displayHours}h",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.displaySmall,
                                    maxLines = 1
                                )
                            }
                        }
                    } else {
                        Text(
                            text = uiState.timerState.formattedTime,
                            maxLines = 1,
                            autoSize = TextAutoSize.StepBased(maxFontSize = LocalTextStyle.current.fontSize)
                        )
                    }
                }
            }

            Spacer(Modifier.heightIn(min = AppTheme.dimens.spacingHuge))

            QuickTimeButtons(
                quickTimes = uiState.quickTimes,
                onQuickTimeClick = { mins ->
                    coroutineScope.launch {
                        selectedMinutes.animateTo(
                            targetValue = mins / 60f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                        onAction(HomeUiAction.UpdateLastSelectedMinutes(mins))
                    }
                },
                onQuickTimeLongClick = { index ->
                    editingIndex = index
                    showEditSheet = true
                },
                onAddClick = { index ->
                    editingIndex = index
                    showEditSheet = true
                }
            )
        }
    }

    if (uiState.showNotificationRationale) {
        AlertDialog(
            onDismissRequest = { onAction(HomeUiAction.DismissPermissionPrompts) },
            title = { Text(stringResource(R.string.home_notification_permission_title)) },
            text = { Text(stringResource(R.string.home_notification_permission_rationale)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(HomeUiAction.DismissPermissionPrompts)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                ) {
                    Text(stringResource(R.string.home_notification_permission_grant))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(HomeUiAction.DismissPermissionPrompts) }) {
                    Text(stringResource(R.string.home_notification_permission_dismiss))
                }
            }
        )
    }

    if (uiState.showNotificationSettingsPrompt) {
        AlertDialog(
            onDismissRequest = { onAction(HomeUiAction.DismissPermissionPrompts) },
            title = { Text(stringResource(R.string.home_notification_permission_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingSmall)) {
                    Text(stringResource(R.string.home_notification_permission_rationale))
                    Text(
                        text = stringResource(R.string.home_notification_permission_settings_prompt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(HomeUiAction.DismissPermissionPrompts)
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.home_notification_permission_open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(HomeUiAction.DismissPermissionPrompts) }) {
                    Text(stringResource(R.string.home_notification_permission_dismiss))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onNavigateToSettings: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.home_title),
                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.headlineLarge
            )
        },
        actions = {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.safeSharedElement(SharedElementKey.ActionButtonGearToCross)
            ) {
                Icon(
                    painter = rememberAnimatedVectorPainter(
                        AnimatedImageVector.animatedVectorResource(R.drawable.avd_gear_to_close),
                        false
                    ),
                    contentDescription = stringResource(R.string.home_settings_description)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeFab(
    timerState: TimerState,
    onNavigateToTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    if (timerState is TimerState.Idle) {
        ExtendedFloatingActionButton(
            onClick = onNavigateToTimer,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.safeSharedElement(SharedElementKey.Fab)
        ) {
            Icon(painterResource(R.drawable.ic_moon_stars), contentDescription = null)
            Spacer(Modifier.width(AppTheme.dimens.spacingNormal))
            Text(
                stringResource(R.string.home_start_timer),
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        val fabHeight = 56.dp
        val fabElevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 6.dp,
            focusedElevation = 6.dp,
            hoveredElevation = 8.dp
        )

        SplitButtonLayout(
            modifier = Modifier.height(fabHeight),
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = onNavigateToTimer,
                    shapes = SplitButtonDefaults.leadingButtonShapesFor(fabHeight),
                    elevation = fabElevation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                            .copy(alpha = 0.35f)
                            .compositeOver(OrangeAccent),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .safeSharedElement(SharedElementKey.Fab)
                ) {
                    Icon(painterResource(R.drawable.ic_alarm), contentDescription = null)
                    Spacer(Modifier.width(AppTheme.dimens.spacingNormal))
                    Text(
                        text = stringResource(R.string.home_show_timer),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    onClick = onStopTimer,
                    shapes = SplitButtonDefaults.trailingButtonShapesFor(fabHeight),
                    elevation = fabElevation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                            .copy(alpha = 0.35f)
                            .compositeOver(RedAccent),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .safeSharedElement(SharedElementKey.FabTrailing)
                ) {
                    Icon(painterResource(R.drawable.ic_close), contentDescription = null)
                }
            }
        )
    }
}

@Composable
private fun QuickTimeButtons(
    quickTimes: List<Int>,
    onQuickTimeClick: (Int) -> Unit,
    onQuickTimeLongClick: (index: Int) -> Unit,
    onAddClick: (index: Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        quickTimes.forEachIndexed { index, mins ->
            TimeButton(
                onClick = {
                    if (mins == -1) {
                        onAddClick(index)
                    } else {
                        onQuickTimeClick(mins)
                    }
                },
                onLongClick = {
                    onQuickTimeLongClick(index)
                }
            ) {
                if (mins == -1) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppTheme.dimens.spacingNormal)
                            .dashedBorder(
                                AppTheme.dimens.borderThickness,
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                MaterialTheme.shapes.medium,
                                AppTheme.dimens.dashLength,
                                AppTheme.dimens.dashGap
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = stringResource(R.string.home_add_quick_time_description),
                            modifier = Modifier.size(AppTheme.dimens.quickTimeAddIconSize),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }
                } else {
                    Text(text = "$mins")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickTimeEditSheet(
    initialValue: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Expanded)
    val focusRequester = remember { FocusRequester() }
    val initialText = if (initialValue == -1) "" else initialValue.toString()
    var editValue by remember {
        mutableStateOf(
            TextFieldValue(
                initialText,
                TextRange(initialText.length)
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        QuickTimeEditContent(
            editValue = editValue,
            onValueChange = { editValue = it },
            onSave = onSave,
            focusRequester = focusRequester
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun QuickTimeEditContent(
    editValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSave: (Int) -> Unit,
    focusRequester: FocusRequester
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimens.spacingExtraLarge)
            .padding(bottom = AppTheme.dimens.spacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingLarge)
    ) {
        Text(
            text = stringResource(R.string.home_quick_time_dialog_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = editValue,
            onValueChange = { if (it.text.isDigitsOnly()) onValueChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
            suffix = {
                Text(
                    text = stringResource(R.string.home_quick_time_unit_min),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val mins = editValue.text.toIntOrNull() ?: 0
                    if (mins in 1..120) {
                        onSave(mins)
                    }
                }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        DefaultButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val mins = editValue.text.toIntOrNull() ?: 0
                if (mins in 1..120) {
                    onSave(mins)
                }
            },
        ) {
            Text(
                text = stringResource(R.string.home_quick_time_save),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuickTimeEditSheetPreview() {
    AppTheme {
        QuickTimeEditContent(
            editValue = TextFieldValue("30", TextRange(2)),
            onValueChange = {},
            onSave = {},
            focusRequester = remember { FocusRequester() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                quickTimes = listOf(15, 30, 45, 60, -1),
                lastSelectedMinutes = 50,
                timerState = TimerState.Idle()
            ),
            onNavigateToTimer = {},
            onNavigateToSettings = {},
            onAction = {},
            onHasNotificationPermission = { true },
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
