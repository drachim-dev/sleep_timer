package dr.achim.sleep_timer.presentation.onboarding

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.ui.components.DefaultButton
import dr.achim.sleep_timer.ui.components.DefaultTextButton
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens
import kotlinx.coroutines.launch
import kotlin.math.max

data class OnboardingPage(
    val titleRes: Int,
    val descriptionRes: Int,
    val imageRes: Int
)

private val onboardingPages = listOf(
    OnboardingPage(
        titleRes = R.string.onboarding_music_title,
        descriptionRes = R.string.onboarding_music_desc,
        imageRes = R.drawable.img_music
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_automate_title,
        descriptionRes = R.string.onboarding_automate_desc,
        imageRes = R.drawable.img_automate
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_interruption_title,
        descriptionRes = R.string.onboarding_interruption_desc,
        imageRes = R.drawable.img_interruption
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_sleep_title,
        descriptionRes = R.string.onboarding_sleep_desc,
        imageRes = R.drawable.img_sleep
    ),
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(
                modifier = Modifier
                    .padding(AppTheme.dimens.spacingMedium)
                    .navigationBarsPadding()
            ) {
                if (isLastPage) {
                    DefaultButton(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.onboarding_get_started))
                    }
                } else {
                    BottomNavRow(
                        modifier = Modifier.fillMaxWidth(),
                        leftContent = {
                            DefaultTextButton(onClick = onComplete) {
                                Text(stringResource(R.string.onboarding_skip))
                            }
                        },
                        rightContent = {
                            DefaultButton(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                            ) {
                                Text(text = stringResource(R.string.onboarding_next))
                            }
                        },
                        centerContent = {
                            PagerIndicator(pagerState)
                        }
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { pageIndex ->
            val page = onboardingPages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppTheme.dimens.spacingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(page.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacingExtraLarge))

                Text(
                    text = stringResource(page.titleRes),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacingMedium))

                Text(
                    text = stringResource(page.descriptionRes),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PagerIndicator(pagerState: PagerState) {
    Row {
        repeat(pagerState.pageCount) { iteration ->
            val isCurrentPage = pagerState.currentPage == iteration
            val color by animateColorAsState(
                targetValue = if (isCurrentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "IndicatorColor"
            )
            Box(
                modifier = Modifier
                    .padding(AppTheme.dimens.spacingExtraSmall)
                    .size(AppTheme.dimens.pageIndicator)
                    .background(color, CircleShape),
            )
        }
    }
}

@Composable
fun BottomNavRow(
    leftContent: @Composable () -> Unit,
    centerContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        // Measure both buttons to find the max width
        val leftPlaceable = subcompose("left", leftContent).map { it.measure(looseConstraints) }
        val rightPlaceable = subcompose("right", rightContent).map { it.measure(looseConstraints) }

        val leftWidth = leftPlaceable.maxOfOrNull { it.width } ?: 0
        val rightWidth = rightPlaceable.maxOfOrNull { it.width } ?: 0
        val maxWidth = max(leftWidth, rightWidth)

        // Remeasure buttons with the same width
        val buttonConstraints =
            constraints.copy(minWidth = maxWidth, maxWidth = maxWidth, minHeight = 0)
        val finalLeftPlaceable =
            subcompose("left_final", leftContent).map { it.measure(buttonConstraints) }
        val finalRightPlaceable =
            subcompose("right_final", rightContent).map { it.measure(buttonConstraints) }

        // Measure center content
        val centerPlaceable =
            subcompose("center", centerContent).map { it.measure(looseConstraints) }

        val height = max(
            max(finalLeftPlaceable.maxOfOrNull { it.height } ?: 0,
                finalRightPlaceable.maxOfOrNull { it.height } ?: 0),
            centerPlaceable.maxOfOrNull { it.height } ?: 0
        )

        layout(constraints.maxWidth, height) {
            finalLeftPlaceable.forEach { it.place(0, (height - it.height) / 2) }
            finalRightPlaceable.forEach {
                it.place(
                    constraints.maxWidth - it.width,
                    (height - it.height) / 2
                )
            }
            centerPlaceable.forEach {
                it.place(
                    (constraints.maxWidth - it.width) / 2,
                    (height - it.height) / 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    AppTheme {
        OnboardingScreen(onComplete = {})
    }
}
