package dr.achim.sleep_timer.presentation.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
    val price by viewModel.price.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val sheetSnackbarHostState = remember { SnackbarHostState() }

    val purchaseErrorMessage = stringResource(R.string.error_purchase_failure)
    val restoreErrorMessage = stringResource(R.string.error_restore_failure)

    // snackbar success messages are being handled by viewModel
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                PurchaseEvent.PurchaseComplete,
                PurchaseEvent.RestoreSuccess ->
                    onBack()

                PurchaseEvent.PurchaseError,
                PurchaseEvent.PurchaseAborted ->
                    sheetSnackbarHostState.showSnackbar(purchaseErrorMessage)

                PurchaseEvent.RestoreError ->
                    sheetSnackbarHostState.showSnackbar(restoreErrorMessage)
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        PaywallSheetContent(
            price = price,
            onPurchase = { viewModel.purchase(context.findActivity()) },
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
private fun PaywallSheetContent(
    price: String,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = AppTheme.dimens.spacingLarge)
            .padding(horizontal = AppTheme.dimens.spacingLarge),
    ) {
        Text(
            text = buildAnnotatedString {
                appendLine(stringResource(R.string.paywall_title_part_1))
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(R.string.paywall_title_part_2))
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
            text = stringResource(R.string.paywall_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingExtraLarge))

        FeatureList()

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingExtraLarge))

        PurchaseCard(price, onPurchase)

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingSmall))

        TextButton(
            onClick = onRestore,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = stringResource(R.string.settings_restore_purchases),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun FeatureList() {
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
        FeatureItem(
            icon = painterResource(R.drawable.ic_open_in_new),
            title = stringResource(R.string.paywall_feature_auto_open_app_title),
            description = stringResource(R.string.paywall_feature_auto_open_app_description),
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
private fun PurchaseCard(price: String, onPurchase: () -> Unit) {
    val isLoading = price.isEmpty()
    Button(
        onClick = onPurchase,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = PaddingValues(AppTheme.dimens.spacingMedium),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimens.spacingMedium),
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
                        text = price,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(AppTheme.dimens.spacingMedium))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = null,
                        modifier = Modifier.padding(AppTheme.dimens.spacingNormal),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
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
                price = "$3.99",
                onPurchase = {},
                onRestore = {},
            )
        }
    }
}
