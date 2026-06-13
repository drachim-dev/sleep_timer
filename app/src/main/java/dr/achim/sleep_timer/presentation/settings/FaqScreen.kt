package dr.achim.sleep_timer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.ui.components.SectionTitle
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.faq_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.settings_back_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding + PaddingValues(AppTheme.dimens.spacingMedium)),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacingLarge)
        ) {
            FaqItem(
                title = stringResource(R.string.faq_timer_no_alarm_title),
                description = stringResource(R.string.faq_timer_no_alarm_description)
            )
            FaqItem(
                title = stringResource(R.string.faq_timer_no_wifi_title),
                description = stringResource(R.string.faq_timer_no_wifi_description)
            )
            FaqItem(
                title = stringResource(R.string.faq_timer_no_bluetooth_title),
                description = stringResource(R.string.faq_timer_no_bluetooth_description)
            )
            FaqItem(
                title = stringResource(R.string.faq_uninstall_title),
                description = stringResource(R.string.faq_uninstall_description)
            )
        }
    }
}

@Composable
fun FaqItem(
    title: String,
    description: String
) {
    Column {
        SectionTitle(text = title)
        Text(text = description)
    }
}

@Preview(showBackground = true)
@Composable
private fun FaqScreenPreview() {
    AppTheme {
        FaqScreen(onBack = {})
    }
}
