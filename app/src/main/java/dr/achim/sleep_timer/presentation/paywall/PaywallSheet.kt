package dr.achim.sleep_timer.presentation.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.findActivity
import dr.achim.sleep_timer.model.PurchaseEvent
import dr.achim.sleep_timer.presentation.settings.StoreProductUiModel
import dr.achim.sleep_timer.ui.components.DiagonalRibbon
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaywallSheet(
    onBack: () -> Unit,
    viewModel: PaywallViewModel = koinViewModel(),
) {
    val product by viewModel.productUiModel.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetSnackbarHostState = remember { SnackbarHostState() }

    ObservePaywallEvents(
        viewModel = viewModel,
        snackbarHostState = sheetSnackbarHostState,
        onComplete = onBack,
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        PaywallSheetContent(
            product = product,
            onPurchase = { id -> viewModel.purchase(context.findActivity(), id) },
            onRestore = viewModel::restorePurchases,
        )

        SnackbarHost(
            hostState = sheetSnackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = AppTheme.dimens.spacingMedium),
        )
    }
}

@Composable
internal fun ObservePaywallEvents(
    viewModel: PaywallViewModel,
    snackbarHostState: SnackbarHostState,
    onComplete: () -> Unit,
) {
    val purchaseErrorMessage = stringResource(R.string.error_purchase_failure)
    val restoreErrorMessage = stringResource(R.string.error_restore_failure)

    // snackbar success messages are being handled by viewModel
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                PurchaseEvent.PurchaseComplete,
                PurchaseEvent.RestoreSuccess ->
                    onComplete()

                PurchaseEvent.PurchaseError,
                PurchaseEvent.PurchaseAborted ->
                    snackbarHostState.showSnackbar(purchaseErrorMessage)

                PurchaseEvent.RestoreError ->
                    snackbarHostState.showSnackbar(restoreErrorMessage)
            }
        }
    }
}

@Composable
internal fun PaywallSheetContent(
    product: StoreProductUiModel?,
    onPurchase: (id: String) -> Unit,
    onRestore: () -> Unit,
) {
    PaywallBody(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = AppTheme.dimens.spacingLarge)
            .padding(horizontal = AppTheme.dimens.spacingLarge),
    ) {
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingExtraLarge))
        PaywallPurchaseFooter(
            product = product,
            onPurchase = onPurchase,
            onRestore = onRestore,
        )
    }
}

@Composable
internal fun PaywallBody(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = { PaywallHeader() },
    content: @Composable () -> Unit = { FeatureList() },
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        header()
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingExtraLarge))
        content()
        bottomContent?.invoke(this)
    }
}

@Composable
internal fun PaywallHeader(
    titlePart1: String = stringResource(R.string.paywall_title_part_1),
    titlePart2: String = stringResource(R.string.paywall_title_part_2),
    subtitle: String = stringResource(R.string.paywall_subtitle),
) {
    Column {
        Text(
            text = buildAnnotatedString {
                appendLine(titlePart1)
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(titlePart2)
                }
            },
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 0.9.em,
            ),
        )
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingNormal))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@Composable
internal fun PaywallPurchaseFooter(
    product: StoreProductUiModel?,
    onPurchase: (id: String) -> Unit,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PurchaseCard(product = product, onPurchase = onPurchase)

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingSmall))

        TextButton(onClick = onRestore) {
            Text(
                text = stringResource(R.string.paywall_restore_purchases),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
internal fun FeatureList() {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingMedium)) {
        FeatureItem(
            icon = painterResource(R.drawable.ic_ad_off),
            title = stringResource(R.string.paywall_feature_no_ads_title),
            description = stringResource(R.string.paywall_feature_no_ads_description),
        )
        FeatureItem(
            icon = painterResource(R.drawable.ic_apps),
            title = stringResource(R.string.paywall_feature_quick_settings_tile_title),
            description = stringResource(R.string.paywall_feature_quick_settings_tile_description),
        )
        FeatureItem(
            icon = painterResource(R.drawable.ic_more_time),
            title = stringResource(R.string.paywall_feature_lights_off_delay_title),
            description = stringResource(R.string.paywall_feature_lights_off_delay_description),
        )
    }
}

@Composable
private fun FeatureItem(
    icon: Painter,
    title: String,
    description: String,
    iconContainerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    iconColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    Box(modifier = Modifier.clipToBounds()) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            shapes = ListItemDefaults.shapes(shape = MaterialTheme.shapes.largeIncreased),
            supportingContent = {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(AppTheme.dimens.quickLaunchAppIconSize)
                        .background(
                            iconContainerColor.copy(alpha = 0.2f),
                            MaterialTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.dimens.quickLaunchIconSize),
                        tint = iconColor,
                    )
                }
            },
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        DiagonalRibbon { Text(stringResource(R.string.common_pro)) }
    }
}

@Composable
internal fun PurchaseCard(product: StoreProductUiModel?, onPurchase: (id: String) -> Unit) {
    val isLoading = product == null || product.price.isEmpty()
    Button(
        onClick = {
            if (product != null) {
                onPurchase(product.id)
            }
        },
        enabled = !isLoading && !product.isPurchased,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = ButtonDefaults.MediumContentPadding,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.paywall_purchase_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text = stringResource(R.string.paywall_purchase_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        ),
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(
                        text = product.price,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(AppTheme.dimens.spacingMedium))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
            PaywallSheetContent(
                product = StoreProductUiModel(
                    id = "pro",
                    title = "Sleep Timer Pro",
                    description = "Unlock Pro",
                    price = "$3.99",
                    isPurchased = false,
                ),
                onPurchase = {},
                onRestore = {},
            )
        }
    }
}
