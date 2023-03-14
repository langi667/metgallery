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
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.HyperlinkText
import de.stefanlang.metgallerybrowser.ui.common.LoadingStateHint
import de.stefanlang.metgallerybrowser.ui.theme.Dimen
import com.google.accompanist.flowlayout.FlowRow
import de.stefanlang.metgallerybrowser.data.repositories.ImageRepositoryEntry
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
        ContentView(state.value, images, viewModel.selectedImage.value?.result?.getOrNull())

        val selectedImage = viewModel.selectedImage.value?.resultValue

        if (selectedImage != null && state.value is ObjectDetailViewModel.State.LoadedWithSuccess) {
            Box(modifier = Modifier
                .padding(Dimen.s)
                .fillMaxSize()
                .background(MaterialTheme.colors.onPrimary.copy(alpha = 0.7f))
                .clickable {
                    viewModel.deselectImage()
                }) {
                RoundedImageView(modifier = Modifier.fillMaxSize(), image = selectedImage?.asImageBitmap(), alignment = Alignment.Center)
            }

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
                    contentDescription = "Back" // TODO: localise
                )
            }
        }
    )
}

@Composable
private fun ContentView(state: ObjectDetailViewModel.State, images: List<ImageRepositoryEntry>, selectedImage: Bitmap?) {
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
private fun METObjectDetailView(metObjectUIRepresentable: METObjectUIRepresentable, loadedImages: List<ImageRepositoryEntry>) {
    LazyColumn {
        items(metObjectUIRepresentable.entries.size + 1){currIndex ->
            if (currIndex == 0){
                METGalleryView(metObjectUIRepresentable,loadedImages)
            }
            else {
                METObjectEntryView(metObjectUIRepresentable.entries[currIndex - 1])
            }
        }
    }
}

@Composable
private fun METGalleryView(metObjectUIRepresentable: METObjectUIRepresentable,
                           loadedImages: List<ImageRepositoryEntry>) {

    val images = metObjectUIRepresentable.metObject.imageData
    val width = (LocalConfiguration.current.screenWidthDp - 2 * Dimen.s.value) / 3
    val height = 150.dp * (4 / 3) // default ratio of the primary image

    FlowRow() {
        repeat(images.size) { it ->

            var painter: Painter? = null
            val currImageData = images.getOrNull(it)

            val imageData = loadedImages.firstOrNull { currEntry ->
                currImageData?.containsURL(currEntry.query) == true
            }

            imageData?.result?.getOrNull()?.let {loadedImage ->
                painter = BitmapPainter(loadedImage.asImageBitmap())
            }

            // error during loading
            imageData?.result?.exceptionOrNull()?.let { _ ->
                painter = painterResource(id = R.drawable.error_state_img)
            }

            val viewModel: ObjectDetailViewModel = viewModel()

            RoundedImageView(
                modifier = Modifier.clickable { imageData?.isSuccess
                    viewModel.onImageSelected(imageData)
                }.size(width.dp, height)
                , painter)
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

// endregion