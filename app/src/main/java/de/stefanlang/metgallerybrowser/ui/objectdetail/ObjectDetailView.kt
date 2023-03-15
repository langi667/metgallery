package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepositoryEntry
import de.stefanlang.metgallerybrowser.domain.ImageLoadResult
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.HyperlinkText
import de.stefanlang.metgallerybrowser.ui.common.LoadingStateHint
import de.stefanlang.metgallerybrowser.ui.theme.Dimen
import de.stefanlang.uicore.RoundedImageView

// region Public API

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectDetailView(navController: NavController, objectID: Int) {
    val viewModel: ObjectDetailViewModel = viewModel()
    val state = viewModel.state.collectAsState()

    viewModel.loadObjectForID(objectID)
    val images = viewModel.images.toList()

    Scaffold(topBar = {
        TopBar(navController)
    }) {
        ContentView(state.value, images)
        val selectedImage = (viewModel.selectedImage.value as? ImageLoadResult.Success)?.image

        if (selectedImage != null && state.value is ObjectDetailViewModel.State.LoadedWithSuccess) {
            ImageDetailView(viewModel, selectedImage)
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {
    val viewModel: ObjectDetailViewModel = viewModel()

    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = {
                viewModel.onBackPressed(navController)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.common_back)
                )
            }
        }
    )
}

@Composable
private fun ContentView(
    state: ObjectDetailViewModel.State,
    images: List<ImageLoadResult>
) {
    Box(modifier = Modifier.padding(Dimen.s)) {
        when (state) {
            is ObjectDetailViewModel.State.Loading -> {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.m)
                )
                LoadingStateHint()
            }

            is ObjectDetailViewModel.State.LoadedWithError -> {
                ErrorStateHint()
            }

            is ObjectDetailViewModel.State.LoadedWithSuccess -> {
                METObjectDetailView(state.metObjectUIRepresentable, images)
            }
        }
    }
}

@Composable
private fun METObjectDetailView(
    metObjectUIRepresentable: METObjectUIRepresentable,
    loadedImages: List<ImageLoadResult>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = metObjectUIRepresentable.metObject.title ?: stringResource(id = R.string.object_default_title),
        style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(Dimen.s))
        
        LazyColumn {
            items(metObjectUIRepresentable.entries.size + 1) { currIndex ->
                if (currIndex == 0) {
                    METGalleryView(metObjectUIRepresentable, loadedImages)
                } else {
                    val spacerHeight = if (currIndex == 1) { Dimen.s } else { Dimen.xs }
                    Spacer(modifier = Modifier.height(spacerHeight))
                    METObjectEntryView(metObjectUIRepresentable.entries[currIndex - 1])
                }
            }
        }
    }
}

@Composable
private fun METGalleryView(
    metObjectUIRepresentable: METObjectUIRepresentable,
    loadedImages: List<ImageLoadResult>
) {
    val images = metObjectUIRepresentable.metObject.imageData
    val width = (LocalConfiguration.current.screenWidthDp - 2 * Dimen.s.value) / 3
    val height = width * (4 / 3) // default ratio of the primary image

    FlowRow() {
        repeat(images.size) { it ->

            val currImageData = images.getOrNull(it)

            val imageData = loadedImages.firstOrNull { currEntry ->
                currImageData?.containsURL(currEntry.url) == true
            }

            val painter: Painter? = when (imageData) {
                is ImageLoadResult.Success -> BitmapPainter(imageData.image.asImageBitmap())
                is ImageLoadResult.Failure -> painterResource(id = R.drawable.error_state_img)
                else-> {null}
            }

            val isClickable = imageData is ImageLoadResult.Success
            val viewModel: ObjectDetailViewModel = viewModel()

            RoundedImageView(
                modifier = Modifier
                    .clickable { isClickable
                        viewModel.onImageSelected(imageData)
                    }
                    .size(width.dp, height.dp), painter
            )
        }
    }
}

@Composable
private fun METObjectEntryView(entry: METObjectUIRepresentable.Entry) {
    Column {
        Text(text = entry.name, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(Dimen.xxs))
        HyperlinkText(
            fullText = entry.value,
            modifier = Modifier.padding(start = Dimen.xs),
            hyperlinks = entry.hyperlinks,
            linkTextColor = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun ImageDetailView(viewModel: ObjectDetailViewModel, selectedImage: Bitmap) {
    Box(modifier = Modifier
        .padding(Dimen.s)
        .fillMaxSize()
        .background(MaterialTheme.colors.onPrimary.copy(alpha = 0.7f))
        .clickable {
            viewModel.deselectImage()
        }) {
        RoundedImageView(
            modifier = Modifier.fillMaxSize(),
            image = selectedImage.asImageBitmap(),
            alignment = Alignment.Center
        )
    }
}

// endregion