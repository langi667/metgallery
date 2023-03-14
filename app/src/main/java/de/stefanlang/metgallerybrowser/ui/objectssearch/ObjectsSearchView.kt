package de.stefanlang.metgallerybrowser.ui.objectssearch

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.data.models.METObjectsSearchResult
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.IdleStateHint
import de.stefanlang.metgallerybrowser.ui.common.NoSearchResultsHint
import de.stefanlang.metgallerybrowser.ui.theme.Dimen
import kotlinx.coroutines.*

// region Public API
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectsSearchView(
    navController: NavController,
    viewModel: ObjectsSearchViewModel = viewModel()
) {
    Scaffold(topBar = {
        TopBar()
    }) {
        ContentView(navController, viewModel)
    }
}

// endregion

// region Private API

@Composable
private fun TopBar() {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) }
    )
}

@Composable
private fun ContentView(
    navController: NavController,
    viewModel: ObjectsSearchViewModel
) {
    val searchText = viewModel.searchQuery.collectAsState()
    val state = viewModel.state.collectAsState()
    val isSearching = viewModel.isSearching.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TextField(
            value = searchText.value,
            onValueChange = viewModel::onSearchQueryChanged,
            keyboardOptions = KeyboardOptions(
                imeAction = androidx.compose.ui.text.input.ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    viewModel.startSearch()
                }
            ),

            trailingIcon = {Icon(Icons.Default.Clear,
                contentDescription = "clear text", // TODO: localise
                modifier = Modifier
                    .clickable {
                        viewModel.onSearchClear()
                    }
            )},

            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.s),
            placeholder = {
                Text(text = stringResource(R.string.search_placeholder))
            }
        )

        Spacer(modifier = Modifier.height(Dimen.s))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        ) {
            if (isSearching.value) {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.m)
                )
            }

            ViewForState(viewModel, state.value) { objectID ->
                viewModel.onObjectIDSelected(objectID, navController)
            }
        }
    }
}

@Composable
private fun ViewForState(
    viewModel: ObjectsSearchViewModel,
    state: ObjectsSearchViewModel.State,
    onItemClick: (objectID: Int) -> Unit
) {

    when (state) {
        is ObjectsSearchViewModel.State.Idle -> {
            IdleStateHint()
        }

        is ObjectsSearchViewModel.State.FinishedWithSuccess -> {
            if (state.hasSearchResults) {
                ObjectsSearchResultList(state.objectsSearch, onItemClick)
            } else {
                NoSearchResultsHint()
            }
        }

        is ObjectsSearchViewModel.State.FinishedWithError -> {
            ErrorStateHint()
        }
    }
}

@Composable
private fun ObjectsSearchResultList(
    objectsSearchResult: METObjectsSearchResult,
    onItemSelected: (item: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val objectIDs = objectsSearchResult.objectIDs ?: emptyList()
        items(objectIDs) { currObjectID ->
            ObjectsSearchItemView(
                objectID = currObjectID,
                onItemSelected = onItemSelected
            )
        }
    }
}

@Composable
private fun ObjectsSearchItemView(
    objectID: Int,
    onItemSelected: (item: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                onItemSelected(objectID)
            }
    ) {
        Text(
            text = "$objectID", modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Divider(thickness = 1.dp, color = MaterialTheme.colors.secondary)
    }
}

// endregion