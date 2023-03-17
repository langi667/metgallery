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
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.domain.ImageLoadResult
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.metgallerybrowser.ui.common.*
import de.stefanlang.metgallerybrowser.ui.theme.Dimen

// region Public API

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectDetailView(navController: NavController, objectID: Int) {
    val viewModel: ObjectDetailViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState()

    viewModel.loadObjectForID(objectID)
    val images = viewModel.images.toList()

    Scaffold(topBar = {
        TopBar(navController)
    }) {
        ContentView(state.value, images)
        val selectedImage = (viewModel.selectedImage.value as? ImageLoadResult.Success)

        if (selectedImage != null && state.value is ObjectDetailViewModel.State.LoadedWithSuccess) {
            GalleryView(viewModel.allLoadedImages(), selectedImage) {
                viewModel.deselectImage()
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {
    val viewModel: ObjectDetailViewModel = hiltViewModel()

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
        Text(
            text = metObjectUIRepresentable.metObject.title
                ?: stringResource(id = R.string.object_default_title),
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(Dimen.S))
        val viewModel: ObjectDetailViewModel = hiltViewModel()

        LazyColumn {
            items(metObjectUIRepresentable.entries.size + 1) { currIndex ->
                if (currIndex == 0) {
                    GalleryPreviewView(
                        metObjectUIRepresentable.metObject.imageData,
                        loadedImages
                    ) { imageLoadResult ->
                        viewModel.onImageSelected(imageLoadResult)
                    }
                } else {
                    val spacerHeight = if (currIndex == 1) {
                        Dimen.S
                    } else {
                        Dimen.XS
                    }
                    Spacer(modifier = Modifier.height(spacerHeight))
                    METObjectEntryView(metObjectUIRepresentable.entries[currIndex - 1])
                }
            }
        }
    }
}

@Composable
private fun METObjectEntryView(entry: METObjectUIRepresentable.Entry) {
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