package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.HyperlinkText
import de.stefanlang.metgallerybrowser.ui.common.LoadingStateHint
import de.stefanlang.metgallerybrowser.ui.theme.Dimen
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowRow
import de.stefanlang.uicore.RoundedImageView

// region Public API

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectDetailView(navController: NavController, objectID: Int) {
    val viewModel: ObjectDetailViewModel = viewModel()

    val state = viewModel.state.collectAsState()
    viewModel.loadObjectForID(objectID)

    Scaffold(topBar = {
        TopBar(navController)
    }) {
        ContentView(state.value)
    }
}

@Composable
private fun TopBar(navController: NavController) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack() // TODO: call view model
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
private fun ContentView(state: ObjectDetailViewModel.State) {
    Box(modifier = Modifier.padding(Dimen.s)) {
        when (state) {
            is ObjectDetailViewModel.State.Loading -> {
                LoadingStateHint()
            }

            is ObjectDetailViewModel.State.FinishedWithError -> {
                ErrorStateHint()
            }

            is ObjectDetailViewModel.State.FinishedWithSuccess -> {
                METObjectDetailView(state.metObjectUIRepresentable)
            }
        }
    }
}

@Composable
private fun METObjectDetailView(metObjectUIRepresentable: METObjectUIRepresentable) {
    LazyColumn {

        items(metObjectUIRepresentable.entries.size + 1){currIndex ->
            if (currIndex == 0){
                METGalleryView(metObjectUIRepresentable)
            }
            else {
                METObjectEntryView(metObjectUIRepresentable.entries[currIndex - 1])
            }
        }
    }
}

@Composable
private fun METGalleryView(metObjectUIRepresentable: METObjectUIRepresentable) {
    val images = metObjectUIRepresentable.metObject.imageData
    val height = 150.dp
    val width = height * 0.75f

    FlowRow() {
        repeat(images.size) {
            RoundedImageView(
                modifier = Modifier
                    .size(width, height)
                    , null)
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