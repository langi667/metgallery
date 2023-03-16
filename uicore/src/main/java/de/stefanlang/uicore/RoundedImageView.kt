package de.stefanlang.uicore

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun RoundedImageView(
    modifier: Modifier,
    painter: Painter?,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.TopCenter,
    imageScale: ContentScale = ContentScale.FillWidth
) {
    Box(
        modifier = modifier
            .shadow(2.dp, shape = MaterialTheme.shapes.small, spotColor = Color.LightGray)
            .clip(MaterialTheme.shapes.small)
            .padding(1.dp)
            .fillMaxSize()

    ) {
        val overlayAlpha: Float by animateFloatAsState(
            targetValue = if (painter == null) 1f else 0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearEasing,
            )
        )

        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = imageScale,
                alignment = alignment,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxSize()
            )
        }

        LoadingOverlay(alpha = overlayAlpha)
    }
}

@Composable
fun RoundedImageView(
    modifier: Modifier,
    image: ImageBitmap?,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.TopCenter,
    imageScale: ContentScale = ContentScale.FillWidth
) {
    val painter = if(image != null) {
        BitmapPainter(image)
    }
    else {
        null
    }

    RoundedImageView(
        modifier = modifier,
        painter = painter, alignment = alignment,
        imageScale = imageScale,
        contentDescription = contentDescription
    )
}

