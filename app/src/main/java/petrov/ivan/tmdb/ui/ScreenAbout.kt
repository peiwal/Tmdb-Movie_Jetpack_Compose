package petrov.ivan.tmdb.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.ui.theme.TmdbTheme

@Composable
fun ScrenAbout() {
    val uriHandler = LocalUriHandler.current
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        val apiUrlName = stringResource(R.string.api_url)
        val str = stringResource(id = R.string.app_information, apiUrlName)
        val startIndex = str.indexOf(apiUrlName)
        val endIndex = startIndex + apiUrlName.length
        append(str)
        addStyle(
            style = SpanStyle(
                color = TmdbTheme.colors.textPrimary,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            ), start = startIndex, end = str.length
        )
        addStyle(
            style = SpanStyle(
                color = TmdbTheme.colors.textLink,
                fontSize = 16.sp,
                textDecoration = TextDecoration.Underline
            ), start = startIndex, end = endIndex
        )

        // attach a string annotation that stores a URL to the text "link"
        addStringAnnotation(
            tag = "URL",
            annotation = stringResource(id = R.string.api_url) ,
            start = startIndex,
            end = endIndex
        )
    }

    ClickableText(
        modifier = Modifier
            .padding(8.dp)
            .systemBarsPadding()
            .fillMaxWidth(),
        text = annotatedLinkString,
        style = MaterialTheme.typography.subtitle1,
        onClick = {
            annotatedLinkString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}