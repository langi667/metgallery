package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.IdleStateHint
import de.stefanlang.metgallerybrowser.ui.common.LoadingStateHint
import de.stefanlang.metgallerybrowser.ui.common.NoSearchResultsHint
import de.stefanlang.metgallerybrowser.ui.navigation.NavUtil
import de.stefanlang.metgallerybrowser.ui.theme.Dimen

// region Public API
@Composable
fun ObjectsSearchView(
    navController: NavController,
    viewModel: ObjectsSearchViewModel = viewModel()
) {
    val searchText = viewModel.searchQuery.collectAsState()
    val state = viewModel.state.collectAsState()
    val hideKeyboard = state.value is ObjectsSearchViewModel.State.Loading

    if (hideKeyboard) {
        LocalFocusManager.current.clearFocus()
    }

    Column(Modifier.fillMaxSize()) {
        TextField(
            value = searchText.value,
            onValueChange = viewModel::onSearchQueryChanged,
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
            ViewForState(viewModel, state.value) {objectID ->
                NavUtil.navigateToObjectDetail(navController, objectID)
            }
        }
    }
}

// endregion

// region Private API

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

        is ObjectsSearchViewModel.State.Loading -> {
            LoadingStateView()
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
private fun LoadingStateView() {
    Box(contentAlignment = Alignment.TopCenter) {
        LinearProgressIndicator(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.m)
        )
        LoadingStateHint()
    }
}

@Composable
private fun ObjectsSearchResultList(
    objectsSearchResult: ObjectsSearch,
    onItemSelected: (item: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val objectIDs = objectsSearchResult.result?.objectIDs ?: emptyList()
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