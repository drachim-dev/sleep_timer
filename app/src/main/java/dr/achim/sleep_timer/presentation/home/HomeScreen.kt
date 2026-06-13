package dr.achim.sleep_timer.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.model.TimerState
import dr.achim.sleep_timer.ui.components.CircularTimer
import dr.achim.sleep_timer.ui.components.TimeButton
import dr.achim.sleep_timer.ui.dashedBorder
import dr.achim.sleep_timer.ui.sharedElementTransition
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.OrangeAccent
import dr.achim.sleep_timer.ui.theme.RedAccent
import dr.achim.sleep_timer.ui.theme.dimens
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToTimer: (Int?) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        onNavigateToTimer = {
            if (uiState.timerState is TimerState.Idle) {
                onNavigateToTimer(uiState.lastSelectedMinutes)
            } else {
                onNavigateToTimer(null)
            }
        },
        onNavigateToSettings = onNavigateToSettings,
        onAction = viewModel::onAction,
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
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onNavigateToTimer()
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
            HomeFab(
                timerState = uiState.timerState,
                onNavigateToTimer = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionCheck = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            onNavigateToTimer()
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        onNavigateToTimer()
                    }
                },
                onStopTimer = { onAction(HomeUiAction.StopTimer) }
            )
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
            CircularTimer(
                progress = if (uiState.timerState is TimerState.Idle) selectedMinutes.value else uiState.timerState.progress,
                timeText = if (uiState.timerState is TimerState.Idle) {
                    val totalMinutes = (selectedMinutes.value * 60).toInt()
                    formatMinutes(totalMinutes)
                } else {
                    uiState.timerState.formattedTime
                },
                glowEnabled = uiState.glowEnabled,
                glowIntensity = uiState.glowIntensity,
                interactive = uiState.timerState is TimerState.Idle,
                onProgressChange = { newProgress ->
                    if (uiState.timerState !is TimerState.Idle) return@CircularTimer
                    coroutineScope.launch {
                        val snappedProgress = newProgress.coerceAtLeast(0.01f)
                        selectedMinutes.snapTo(snappedProgress)
                        onAction(HomeUiAction.UpdateLastSelectedMinutes((snappedProgress * 60).toInt()))
                    }
                },
                modifier = Modifier.sharedElementTransition(key = "timer")
            )

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
}

@Composable
private fun formatMinutes(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes == 0 -> stringResource(R.string.home_time_hours_only, hours)
        hours > 0 -> stringResource(R.string.home_time_minutes_and_hours, minutes, hours)
        else -> stringResource(R.string.home_time_minutes_only, minutes)
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
                modifier = Modifier.sharedElementTransition(key = "action-button")
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
            modifier = Modifier.sharedElementTransition(key = "fab")
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
                        .sharedElementTransition(key = "fab")
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
                        .sharedElementTransition(key = "fab-trailing")
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
    var editValue by remember { mutableStateOf(TextFieldValue(initialText, TextRange(initialText.length))) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Text(
            text = stringResource(R.string.home_quick_time_dialog_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppTheme.dimens.spacingMedium),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimens.spacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingMedium)
        ) {

            OutlinedTextField(
                value = editValue,
                onValueChange = { if (it.text.isDigitsOnly()) editValue = it },
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .focusRequester(focusRequester),
                suffix = { Text(stringResource(R.string.home_quick_time_unit_min)) },
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
                singleLine = true
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val mins = editValue.text.toIntOrNull() ?: 0
                    if (mins in 1..120) {
                        onSave(mins)
                    }
                }
            ) {
                Text(stringResource(R.string.home_quick_time_save))
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
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
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}