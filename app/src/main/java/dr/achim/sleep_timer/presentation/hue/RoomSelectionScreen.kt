package dr.achim.sleep_timer.presentation.hue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.data.remote.hue.HueGroup
import dr.achim.sleep_timer.data.remote.hue.HueGroupType
import dr.achim.sleep_timer.model.TimerActionSource
import dr.achim.sleep_timer.ui.components.DefaultButton
import dr.achim.sleep_timer.ui.components.EmptyState
import dr.achim.sleep_timer.ui.components.FullScreenLoadingState
import dr.achim.sleep_timer.ui.components.SwitchListItem
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens

@Composable
fun RoomSelectionScreen(
    onBack: () -> Unit,
    onNavigateToDiscovery: (TimerActionSource) -> Unit,
    viewModel: RoomSelectionViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.onAction(RoomSelectionUiAction.Refresh)
        onPauseOrDispose {}
    }

    RoomSelectionContent(
        uiState = uiState,
        onBack = onBack,
        onToggle = { viewModel.onAction(RoomSelectionUiAction.ToggleGroup(it)) },
        onSave = {
            viewModel.onAction(RoomSelectionUiAction.Save)
            onBack()
        },
        toDiscoveryScreen = { onNavigateToDiscovery(uiState.source) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomSelectionContent(
    uiState: RoomSelectionUiState,
    onBack: () -> Unit,
    onToggle: (String) -> Unit,
    onSave: () -> Unit,
    toDiscoveryScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hue_room_selection_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.settings_back_description)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = toDiscoveryScreen) {
                        Icon(
                            painterResource(R.drawable.ic_network_node),
                            contentDescription = stringResource(R.string.hue_discovery_manual_ip)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
    floatingActionButtonPosition = if (uiState is RoomSelectionUiState.Success && uiState.groups.isEmpty()) FabPosition.End else FabPosition.Center,
    floatingActionButton = {
        when (uiState) {
            is RoomSelectionUiState.Loading -> {}
            is RoomSelectionUiState.Success -> {
                if (uiState.groups.isEmpty()) {
                    FloatingActionButton(onClick = toDiscoveryScreen) {
                        Icon(painterResource(R.drawable.ic_search), contentDescription = null)
                    }
                } else {
                    DefaultButton(
                        onClick = onSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimens.spacingMedium)
                    ) {
                        Text(stringResource(R.string.hue_room_selection_save))
                    }
                }
            }
        }
    },
) { innerPadding ->
    when (uiState) {
        is RoomSelectionUiState.Loading -> {
            FullScreenLoadingState(text = stringResource(R.string.hue_room_selection_loading))
        }

        is RoomSelectionUiState.Success -> {
            if (uiState.groups.isEmpty()) {
                EmptyState(
                    title = stringResource(R.string.hue_room_selection_no_rooms),
                    subtitle = stringResource(R.string.hue_room_selection_no_rooms_sub),
                    imageRes = R.drawable.img_no_results
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding + PaddingValues(AppTheme.dimens.spacingMedium),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingExtraExtraSmall)
                ) {
                    itemsIndexed(uiState.groups, key = { _, group -> group.id }) { index, group ->
                        val cornerRadius = AppTheme.dimens.spacingMedium
                        val lastIndex = index == uiState.groups.lastIndex
                        val shape = when {
                            uiState.groups.size == 1 -> RoundedCornerShape(cornerRadius)
                            index == 0 -> RoundedCornerShape(
                                topStart = cornerRadius,
                                topEnd = cornerRadius
                            )

                            lastIndex -> RoundedCornerShape(
                                bottomStart = cornerRadius,
                                bottomEnd = cornerRadius
                            )

                            else -> RectangleShape
                        }

                        RoomItem(
                            group = group,
                            checked = uiState.selectedGroups.contains(group.id),
                            onToggle = { onToggle(group.id) },
                            modifier = Modifier.clip(shape)
                        )
                    }
                }
            }
        }
    }
}

}

@Composable
private fun RoomItem(
    group: HueGroup,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lightCount = group.lights.size
    SwitchListItem(
        checked = checked,
        onCheckedChange = { onToggle() },
        modifier = modifier,
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_light_group),
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                pluralStringResource(
                    R.plurals.hue_room_selection_light_count,
                    lightCount,
                    lightCount
                )
            )
        }
    ) {
        Text(group.name)
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomSelectionContentPreview() {
    AppTheme {
        RoomSelectionContent(
            uiState = RoomSelectionUiState.Success(
                groups = listOf(
                    HueGroup(id = "1", name = "Living Room", type = HueGroupType.ROOM, lights = listOf("1", "2")),
                    HueGroup(id = "2", name = "Bedroom", type = HueGroupType.ROOM, lights = listOf("3")),
                    HueGroup(id = "3", name = "Kitchen", type = HueGroupType.ROOM, lights = listOf("4", "5", "6"))
                ),
                selectedGroups = setOf("1", "2"),
                source = TimerActionSource.START
            ),
            onBack = {},
            onToggle = {},
            onSave = {},
            toDiscoveryScreen = {}
        )
    }
}

