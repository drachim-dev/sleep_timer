package dr.achim.sleep_timer.presentation.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.LocalIsPro
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.findActivity
import dr.achim.sleep_timer.presentation.settings.Confetti
import dr.achim.sleep_timer.presentation.settings.StoreProductUiModel
import dr.achim.sleep_timer.ui.components.DefaultButton
import dr.achim.sleep_timer.ui.components.DefaultTextButton
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens
import org.koin.androidx.compose.koinViewModel

@Composable
fun WhatsNewProScreen(
    onBack: () -> Unit,
    onExploreSettings: () -> Unit,
    viewModel: PaywallViewModel = koinViewModel(),
) {
    val product by viewModel.productUiModel.collectAsStateWithLifecycle()
    val isPro = LocalIsPro.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showConfetti by remember { mutableStateOf(isPro) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.markWhatsNewSeen()
        }
    }

    val handleDismiss = {
        viewModel.markWhatsNewSeen()
        onBack()
    }

    val handleExploreSettings = {
        viewModel.markWhatsNewSeen()
        onExploreSettings()
    }

    ObservePaywallEvents(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        onComplete = handleDismiss,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        WhatsNewProScreenContent(
            isPro = isPro,
            product = product,
            onBack = handleDismiss,
            onExploreSettings = handleExploreSettings,
            onPurchase = { id -> viewModel.purchase(context.findActivity(), id) },
            onRestore = viewModel::restorePurchases,
            snackbarHostState = snackbarHostState,
        )

        if (showConfetti) {
            Confetti(modifier = Modifier.fillMaxSize()) {
                showConfetti = false
            }
        }
    }
}

@Composable
private fun WhatsNewProScreenContent(
    isPro: Boolean,
    product: StoreProductUiModel?,
    onBack: () -> Unit,
    onExploreSettings: () -> Unit,
    onPurchase: (id: String) -> Unit,
    onRestore: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            TopAppBar(
                title = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape,
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = AppTheme.dimens.spacingNormal,
                                vertical = AppTheme.dimens.spacingExtraSmall,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingSmall),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_moon_stars),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = stringResource(R.string.whats_new_badge),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.timer_close_description),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = AppTheme.dimens.spacingLarge)
                    .padding(top = AppTheme.dimens.spacingSmall, bottom = AppTheme.dimens.spacingMedium)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isPro) {
                    DefaultButton(
                        onClick = onExploreSettings,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.whats_new_pro_cta_settings),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(AppTheme.dimens.spacingExtraSmall))
                    DefaultTextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.whats_new_pro_cta_done),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                } else {
                    PaywallPurchaseFooter(
                        product = product,
                        onPurchase = onPurchase,
                        onRestore = onRestore,
                    )
                }
            }
        },
    ) { innerPadding ->
        PaywallBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppTheme.dimens.spacingLarge)
                .padding(top = AppTheme.dimens.spacingSmall, bottom = AppTheme.dimens.spacingLarge),
            header = {
                if (isPro) {
                    PaywallHeader(
                        titlePart1 = stringResource(R.string.whats_new_pro_title_part_1),
                        titlePart2 = stringResource(R.string.whats_new_pro_title_part_2),
                        subtitle = stringResource(R.string.whats_new_pro_subtitle),
                    )
                } else {
                    PaywallHeader()
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WhatsNewProScreenFreePreview() {
    AppTheme {
        CompositionLocalProvider(LocalIsPro provides false) {
            WhatsNewProScreenContent(
                isPro = false,
                product = StoreProductUiModel(
                    id = "pro",
                    title = "Sleep Timer Pro",
                    description = "Unlock Pro",
                    price = "$3.99",
                    isPurchased = false,
                ),
                onBack = {},
                onExploreSettings = {},
                onPurchase = {},
                onRestore = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WhatsNewProScreenUpgradedPreview() {
    AppTheme {
        CompositionLocalProvider(LocalIsPro provides true) {
            WhatsNewProScreenContent(
                isPro = true,
                product = null,
                onBack = {},
                onExploreSettings = {},
                onPurchase = {},
                onRestore = {},
            )
        }
    }
}
