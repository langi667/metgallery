package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.IdleStateHint
import de.stefanlang.metgallerybrowser.ui.common.LoadingStateHint
import de.stefanlang.metgallerybrowser.ui.common.NoSearchResultsHint
import de.stefanlang.metgallerybrowser.ui.theme.Dimen

// region Public API

@Composable
fun ObjectsSearchView(viewModel: ObjectsSearchViewModel) {
    val searchText = viewModel.searchQuery.collectAsState()
    val state = viewModel.state.collectAsState()

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
            ViewForState(viewModel, state.value)
        }
    }
}

// endregion

// region Private API

@Composable
private fun ViewForState(
    viewModel: ObjectsSearchViewModel,
    state: ObjectsSearchViewModel.State
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
                ObjectsSearchResultList(viewModel, state.objectsSearch)
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
                .padding(horizontal = Dimen.s))
        LoadingStateHint()
    }
}

@Composable
private fun ObjectsSearchResultList(
    viewModel: ObjectsSearchViewModel,
    objectsSearchResult: ObjectsSearch
) {
    val query = objectsSearchResult.query

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val objectIDs = objectsSearchResult.result?.objectIDs ?: emptyList()
        items(objectIDs) { currObjectID ->
            ObjectsSearchItemView(currObjectID)
        }
    }
}

@Composable
private fun ObjectsSearchItemView(objectID: Int, showSeparator: Boolean = true) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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