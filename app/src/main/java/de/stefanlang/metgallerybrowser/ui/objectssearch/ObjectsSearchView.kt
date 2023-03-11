package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.data.models.ObjectsSearch
import androidx.compose.foundation.lazy.items
import de.stefanlang.metgallerybrowser.ui.theme.Dimen

@Composable
fun ObjectsSearchView(viewModel: ObjectsSearchViewModel) {
    val searchText = viewModel.searchQuery.collectAsState()
    val state = viewModel.state.collectAsState()

    when (val stateValue = state.value) {
        is ObjectsSearchViewModel.State.Idle -> {
            // TODO:
        }

        is ObjectsSearchViewModel.State.Loading -> {
            // TODO:
        }

        is ObjectsSearchViewModel.State.FinishedWithSuccess -> {
            if (stateValue.hasSearchResults) {
                ObjectsSearchResultList(viewModel, stateValue.objectsSearch)
            }
            else {
                // TODO:
            }
        }

        is ObjectsSearchViewModel.State.FinishedWithError -> {
            // TODO:
        }
    }
}

@Composable
private fun ObjectsSearchResultList(viewModel:ObjectsSearchViewModel,
                                    objectsSearchResult: ObjectsSearch){
    val query = objectsSearchResult.query

    Column(Modifier.fillMaxSize()) {
        TextField(
            value = query,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.s),
            placeholder = {
                Text(text = stringResource(R.string.search_placeholder))
            }
        )

        Spacer(modifier = Modifier.height(Dimen.s))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val objectIDs = objectsSearchResult.result?.objectIDs ?: emptyList()
            items(objectIDs) {currObjectID ->
                ObjectsSearchItemView(currObjectID)
            }
        }
    }
}

@Composable
private fun ObjectsSearchItemView(objectID: Int, showSeparator: Boolean = true) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        Text(
            text = "$objectID", modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Divider(thickness = 1.dp, color = MaterialTheme.colors.secondary)
    }
}