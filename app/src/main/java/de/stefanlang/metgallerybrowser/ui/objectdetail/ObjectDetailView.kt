package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.domain.METObjectUIRepresentable
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.HyperlinkText
import de.stefanlang.metgallerybrowser.ui.common.LoadingStateHint
import de.stefanlang.metgallerybrowser.ui.theme.Dimen

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
            IconButton(onClick = { navController.popBackStack() }) {
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
    METObjectEntriesView(metObjectUIRepresentable.entries)
}

@Composable
private fun METObjectEntriesView(entries: List<METObjectUIRepresentable.Entry>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(entries) { currEntry ->
            METObjectEntryView(currEntry)
            Spacer(modifier = Modifier.height(Dimen.xs))
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