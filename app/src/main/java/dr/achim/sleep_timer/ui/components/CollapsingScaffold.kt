package dr.achim.sleep_timer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class CollapsingHeaderState(
    val maxHeaderHeight: Dp,
    val listState: LazyListState
) {
    var maxHeaderHeightPx: Float = 0f

    val scrollOffsetProvider: () -> Float = {
        if (listState.firstVisibleItemIndex > 0) maxHeaderHeightPx
        else listState.firstVisibleItemScrollOffset.toFloat().coerceAtMost(maxHeaderHeightPx)
    }

    val isCollapsed: Boolean
        get() = scrollOffsetProvider() > maxHeaderHeightPx * 0.7f
}

@Composable
fun rememberCollapsingHeaderState(
    maxHeaderHeight: Dp = 300.dp,
    listState: LazyListState = rememberLazyListState()
): CollapsingHeaderState {
    val density = LocalDensity.current
    return remember(maxHeaderHeight, listState, density) {
        CollapsingHeaderState(maxHeaderHeight, listState).apply {
            maxHeaderHeightPx = with(density) { maxHeaderHeight.toPx() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingScaffold(
    modifier: Modifier = Modifier,
    state: CollapsingHeaderState = rememberCollapsingHeaderState(),
    topBar: @Composable (isCollapsed: Boolean) -> Unit,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    collapsingHeader: @Composable BoxScope.(scrollOffsetProvider: () -> Float, isCollapsed: Boolean) -> Unit,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = { topBar(state.isCollapsed) },
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = containerColor,
            contentColor = contentColor,
            contentWindowInsets = contentWindowInsets,
        ) { innerPadding ->
            val statusBarsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                content(
                    PaddingValues(
                        top = state.maxHeaderHeight + statusBarsTop - innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
            }
        }

        // Collapsing Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .height(state.maxHeaderHeight)
                .graphicsLayer {
                    val currentOffset = state.scrollOffsetProvider()
                    val fraction = (currentOffset / state.maxHeaderHeightPx).coerceIn(0f, 1f)

                    translationY = -currentOffset
                    alpha = 1f - fraction
                    scaleX = 1f - (fraction * 0.5f)
                    scaleY = scaleX
                }
        ) {
            collapsingHeader(state.scrollOffsetProvider, state.isCollapsed)
        }
    }
}