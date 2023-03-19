package de.stefanlang.core.domain.hintview

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.stefanlang.core.api.R
import de.stefanlang.core.domain.Dimen

// region Public API

@Composable
fun HintView(image: Painter, text: String) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(Dimen.S)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Image(
            painter = image,
            contentDescription = null
        )
    }
}

@Composable
fun HintView(@DrawableRes imgRes: Int, @StringRes textRes: Int) {
    HintView(painterResource(id = imgRes), stringResource(id = textRes))
}

@Composable
fun IdleStateHint() {
    HintView(imgRes = R.drawable.idle_state_img, textRes = R.string.idle_state_hint)
}

@Composable
fun NoResultsHint() {
    HintView(imgRes = R.drawable.no_results_state_img, textRes = R.string.no_results_state_hint)
}

@Composable
fun ErrorStateHint() {
    HintView(imgRes = R.drawable.error_state_img, textRes = R.string.error_state_hint)
}

@Composable
fun LoadingStateHint() {
    HintView(imgRes = R.drawable.loading_state_img, textRes = R.string.loading_state_hint)
}

// endregion


