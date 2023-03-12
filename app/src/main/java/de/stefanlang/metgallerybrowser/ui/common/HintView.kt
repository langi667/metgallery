package de.stefanlang.metgallerybrowser.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import de.stefanlang.metgallerybrowser.Greeting
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.ui.theme.Dimen
import de.stefanlang.metgallerybrowser.ui.theme.METGalleryBrowserTheme

@Composable
fun HintView(image: Painter, text: String) {
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Image(painter = image,
            contentDescription = null
        )

        Text(text = text,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(Dimen.s).fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun HintView(@DrawableRes imgRes: Int, @StringRes textRes: Int) {
    HintView(painterResource(id = imgRes), stringResource(id = textRes))
}

@Composable
fun IdleStateHint() {
     HintView(imgRes = R.drawable.idle_state_img, textRes = R.string.idle_state_hint )
}

@Composable
fun NoSearchResultsHint() {
    HintView(imgRes = R.drawable.no_results_state_img, textRes = R.string.no_results_state_hint )
}

@Composable
fun ErrorStateHint() {
    HintView(imgRes = R.drawable.error_state_img, textRes = R.string.error_state_hint )
}
