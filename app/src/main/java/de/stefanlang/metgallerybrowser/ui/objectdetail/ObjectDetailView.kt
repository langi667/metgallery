package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import de.stefanlang.core.ui.HyperlinkText
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.models.ImageLoadResult
import de.stefanlang.metgallerybrowser.ui.common.*
import de.stefanlang.metgallerybrowser.ui.theme.Dimen
import de.stefanlang.metgallerybrowser.utils.METObjectEntryBuilder

// region Public API

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectDetailView(
    navController: NavController,
    objectID: Int,
    viewModel: ObjectDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    viewModel.loadObjectForID(objectID)
    val images = viewModel.images.toList()

    Scaffold(topBar = {
        TopBar(navController, viewModel)
    }) {
        ContentView(viewModel, state.value, images)
        val selectedImage = (viewModel.selectedImage.value as? ImageLoadResult.Success)

        if (selectedImage != null && state.value is ObjectDetailViewModel.State.LoadedWithSuccess) {
            GalleryView(viewModel.allLoadedImages(), selectedImage) {
                viewModel.deselectImage()
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController, viewModel: ObjectDetailViewModel) {

    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = {
                viewModel.onBackPressed(navController)
            }, modifier = Modifier.testTag(Tags.BACK_BUTTON.name)) {
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
    viewModel: ObjectDetailViewModel,
    state: ObjectDetailViewModel.State,
    images: List<ImageLoadResult>
) {
    Box(modifier = Modifier.padding(Dimen.S)) {
        when (state) {
            is ObjectDetailViewModel.State.Loading -> {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.M)
                        .testTag(Tags.PROGRESSBAR.name)
                )
                LoadingStateHint()
            }

            is ObjectDetailViewModel.State.LoadedWithError -> {
                ErrorStateHint()
            }

            is ObjectDetailViewModel.State.NotFound -> {
                NoResultsHint()
            }

            is ObjectDetailViewModel.State.LoadedWithSuccess -> {
                METObjectDetailView(viewModel, images)
            }
        }
    }
}

@Composable
private fun METObjectDetailView(
    viewModel: ObjectDetailViewModel,
    loadedImages: List<ImageLoadResult>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = viewModel.title
                ?: stringResource(id = R.string.object_default_title),
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(Dimen.S))
        val entries = viewModel.entries
        val imageData = viewModel.imageData

        LazyColumn {
            items(entries.size + 1) { currIndex ->
                if (currIndex == 0) {
                    GalleryPreviewView(
                        imageData,
                        loadedImages
                    ) { selectedImage ->
                        viewModel.onImageSelected(selectedImage)
                    }

                } else {
                    val spacerHeight = if (currIndex == 1) {
                        Dimen.S
                    } else {
                        Dimen.XS
                    }
                    Spacer(modifier = Modifier.height(spacerHeight))
                    METObjectEntryView(entries[currIndex - 1])
                }
            }
        }
    }
}

@Composable
private fun METObjectEntryView(entry: METObjectEntryBuilder.Entry) {
    Column {
        Text(text = entry.name, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(Dimen.XXS))
        HyperlinkText(
            fullText = entry.value,
            modifier = Modifier.padding(start = Dimen.XS),
            hyperlinks = entry.hyperlinks,
            linkTextColor = MaterialTheme.colors.primary
        )
    }
}

// endregion