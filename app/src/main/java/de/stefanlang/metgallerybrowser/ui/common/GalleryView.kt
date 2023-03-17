package de.stefanlang.metgallerybrowser.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import de.stefanlang.metgallerybrowser.domain.ImageLoadResult
import de.stefanlang.metgallerybrowser.ui.theme.Dimen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryView(
    images: List<ImageLoadResult.Success>,
    selectedImage: ImageLoadResult.Success?,
    onImageSelected: ((ImageLoadResult?) -> Unit)
) {
    Box(
        modifier = Modifier
            .padding(Dimen.S)
            .fillMaxSize()
            .background(MaterialTheme.colors.onPrimary.copy(alpha = 0.7f))
            .testTag(Tags.GALLERY_BACKGROUND.name)
    ) {

        val selectedIndex = if (selectedImage != null) {
            var retVal = images.indexOf(selectedImage)
            if (retVal == -1) {
                retVal = 0
            }

            retVal
        } else {
            0
        }

        val state = rememberLazyListState(selectedIndex)
        val snappingLayout = remember(state) { SnapLayoutInfoProvider(state) }
        val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

        LazyRow(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            state = state,
            flingBehavior = flingBehavior
        ) {
            items(images.size) { currIndex ->
                val currImage = images[currIndex]
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .fillParentMaxHeight()
                        .padding(8.dp)
                        .clickable {
                            onImageSelected(currImage)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = currImage.image.asImageBitmap(),
                        contentDescription = currImage.url
                    )
                }
            }
        }
    }
}