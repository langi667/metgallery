package de.stefanlang.feature.objectssearch.ui

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import de.stefanlang.core.domain.Dimen
import de.stefanlang.core.domain.hintview.ErrorStateHint
import de.stefanlang.core.domain.hintview.IdleStateHint
import de.stefanlang.core.domain.hintview.NoResultsHint
import de.stefanlang.feature.objectssearch.model.METObjectsSearchResult
import de.stefanlang.objectssearch.R
import kotlinx.coroutines.*

// region Public API
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectsSearchView(
    navController: NavController,
    viewModel: ObjectsSearchViewModel = hiltViewModel()
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
        title = { Text(text = stringResource(id = R.string.title)) }
    )
}

@Composable
private fun ContentView(
    navController: NavController,
    viewModel: ObjectsSearchViewModel
) {
    val searchText = viewModel.searchQuery.collectAsState()
    val state = viewModel.uiState.collectAsState()
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
            trailingIcon = {
                Icon(Icons.Default.Clear,
                    contentDescription = stringResource(id = R.string.clear_text),
                    modifier = Modifier
                        .clickable {
                            viewModel.onSearchClear()
                        }
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.S)
                .testTag(Tags.SEARCH_FIELD.name),
            placeholder = {
                Text(text = stringResource(R.string.search_placeholder))
            }
        )

        Spacer(modifier = Modifier.height(Dimen.S))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        ) {
            if (isSearching.value) {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.M)
                        .testTag(Tags.PROGRESSBAR.name)
                )
            }

            ViewForState(state.value) { objectID ->
                viewModel.onObjectIDSelected(objectID, navController)
            }
        }
    }
}

@Composable
private fun ViewForState(
    state: ObjectsSearchViewModel.UiState,
    onItemClick: (objectID: Int) -> Unit
) {

    when (state) {
        is ObjectsSearchViewModel.UiState.Idle -> {
            IdleStateHint()
        }

        is ObjectsSearchViewModel.UiState.FinishedWithSuccess -> {
            if (state.hasSearchResults) {
                ObjectsSearchResultList(state.objectsSearch, onItemClick)
            } else {
                NoResultsHint()
            }
        }

        is ObjectsSearchViewModel.UiState.FinishedWithError -> {
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
            .testTag(Tags.SEARCH_RESULTS_LIST.name)
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
            .testTag(Tags.SEARCH_RESULT_ENTRY.name)
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