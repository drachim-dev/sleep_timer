package dr.achim.sleep_timer.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.ui.components.SectionTitle
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens

data class Credit(val title: String, val url: String)

private val iconsFromNounProject = listOf(
    Credit(
        title = "Philips Hue Bridge by Derrick Snider",
        url = "https://thenounproject.com/term/phillips-hue-bridge/2731255"
    ),
    Credit(
        title = "Sleep by Nithinan Tatah",
        url = "https://thenounproject.com/term/sleep/2411493/"
    ),
)

private val imagesFromFreepik = listOf(
    Credit(
        title = "Creativity Illustrations | Cuate Style",
        url = "https://stories.freepik.com/idea"
    ),
    Credit(
        title = "Flashlight Illustrations | Cuate Style",
        url = "https://stories.freepik.com/people"
    ),
    Credit(
        title = "Floating Illustrations | Bro Style",
        url = "https://stories.freepik.com/people"
    ),
    Credit(
        title = "Headphone Illustrations | Amico Style",
        url = "https://stories.freepik.com/music"
    ),
    Credit(
        title = "Mobile marketing Illustrations | Pana Style",
        url = "https://stories.freepik.com/business"
    ),
    Credit(
        title = "Web Illustrations | Cuate Style",
        url = "https://storyset.com/web"
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onBack: () -> Unit
) {
    val libraries by produceLibraries(R.raw.aboutlibraries)
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_credits_title)) },
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
        LibrariesContainer(
            libraries = libraries,
            modifier = Modifier.fillMaxSize(),
            detailMode = LibraryDetailMode.Sheet,
            contentPadding = innerPadding + PaddingValues(vertical = AppTheme.dimens.spacingMedium),
            header = {
                item {
                    CreditsHeader(
                        onCreditClick = { credit ->
                            uriHandler.openUri(credit.url)
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun CreditsHeader(
    modifier: Modifier = Modifier,
    onCreditClick: (Credit) -> Unit
) {
    Column(modifier = modifier) {
        SectionTitle(
            text = stringResource(R.string.credits_noun_project_header),
            contentPadding = PaddingValues(bottom = AppTheme.dimens.spacingSmall) +
                    PaddingValues(horizontal = AppTheme.dimens.spacingMedium)
        )
        iconsFromNounProject.forEach { credit ->
            CreditItem(credit = credit) {
                onCreditClick(credit)
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingLarge))

        SectionTitle(
            text = stringResource(R.string.credits_freepik_header),
            contentPadding = PaddingValues(bottom = AppTheme.dimens.spacingSmall) +
                    PaddingValues(horizontal = AppTheme.dimens.spacingMedium)
        )
        imagesFromFreepik.forEach { credit ->
            CreditItem(credit = credit) {
                onCreditClick(credit)
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacingLarge))

        SectionTitle(
            text = stringResource(R.string.credits_libraries_header),
            contentPadding = PaddingValues(bottom = AppTheme.dimens.spacingSmall) +
                    PaddingValues(horizontal = AppTheme.dimens.spacingMedium)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreditsHeaderPreview() {
    AppTheme {
        CreditsHeader(onCreditClick = {})
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreditItem(
    credit: Credit,
    onClick: (Credit) -> Unit
) {
    ListItem(
        onClick = { onClick(credit) },
        supportingContent = { Text(text = credit.url) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    ) {
        Text(text = credit.title)
    }
}
