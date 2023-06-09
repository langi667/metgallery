package de.stefanlang.feature.objectdetail.ui

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
import de.stefanlang.core.domain.Dimen
import de.stefanlang.core.domain.hintview.ErrorStateHint
import de.stefanlang.core.domain.hintview.LoadingStateHint
import de.stefanlang.core.domain.hintview.NoResultsHint
import de.stefanlang.core.ui.HyperlinkText
import de.stefanlang.feature.objectdetail.R
import de.stefanlang.feature.objectdetail.model.ImageLoadResult
import de.stefanlang.feature.objectdetail.model.METObjectEntryBuilder
import de.stefanlang.metgallerybrowser.ui.common.*

// region Public API

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectDetailView(
    navController: NavController,
    objectID: Int,
    viewModel: ObjectDetailViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()

    viewModel.loadObjectForID(objectID)
    val images = viewModel.images.toList()

    Scaffold(topBar = {
        TopBar(navController, viewModel)
    }) {
        ContentView(viewModel, state.value, images)
        val selectedImage = (viewModel.selectedImage.value as? ImageLoadResult.Success)

        if (selectedImage != null && state.value is ObjectDetailViewModel.UIState.LoadedWithSuccess) {
            GalleryView(viewModel.allLoadedImages(), selectedImage) {
                viewModel.deselectImage()
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController, viewModel: ObjectDetailViewModel) {

    TopAppBar(
        title = { Text(text = stringResource(id = de.stefanlang.core.api.R.string.title)) },
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
    state: ObjectDetailViewModel.UIState,
    images: List<ImageLoadResult>
) {
    Box(modifier = Modifier.padding(Dimen.S)) {
        when (state) {
            is ObjectDetailViewModel.UIState.Loading -> {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.M)
                        .testTag(Tags.PROGRESSBAR.name)
                )
                LoadingStateHint()
            }

            is ObjectDetailViewModel.UIState.LoadedWithError -> {
                ErrorStateHint()
            }

            is ObjectDetailViewModel.UIState.NotFound -> {
                NoResultsHint()
            }

            is ObjectDetailViewModel.UIState.LoadedWithSuccess -> {
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