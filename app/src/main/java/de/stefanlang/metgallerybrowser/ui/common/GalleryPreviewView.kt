package de.stefanlang.metgallerybrowser.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.domain.ImageLoadResult
import de.stefanlang.metgallerybrowser.domain.models.ImageData
import de.stefanlang.metgallerybrowser.ui.theme.Dimen
import de.stefanlang.uicore.RoundedImageView


@Composable
fun GalleryPreviewView(
    imageData: List<ImageData>,
    loadedImages: List<ImageLoadResult>,
    onImageSelected: ((ImageLoadResult?) -> Unit)
) {
    val width = (LocalConfiguration.current.screenWidthDp - 2 * Dimen.S.value) / 3
    val height = width * (4 / 3) // default ratio of the primary image

    FlowRow() {
        repeat(imageData.size) { it ->
            val currImageData = imageData.getOrNull(it)
            val imageLoadResult = loadedImages.firstOrNull { currEntry ->
                currImageData?.containsURL(currEntry.url) == true
            }

            val painter: Painter?
            val contentDescription: String?

            when (imageLoadResult) {
                is ImageLoadResult.Success -> {
                    painter = BitmapPainter(imageLoadResult.image.asImageBitmap())
                    contentDescription = stringResource(
                        id = R.string.preview_content_description,
                        imageLoadResult.url
                    )
                }
                is ImageLoadResult.Failure -> {
                    painter = painterResource(id = R.drawable.error_state_img)
                    contentDescription = null
                }
                else -> {
                    painter = null
                    contentDescription = null
                }
            }

            RoundedImageView(
                modifier = Modifier
                    .clickable {
                        onImageSelected(imageLoadResult)
                    }
                    .size(width.dp, height.dp)
                    .testTag(Tags.GALLERY_IMAGE_PREVIEW.name),
                painter = painter,
                contentDescription = contentDescription
            )
        }
    }
}