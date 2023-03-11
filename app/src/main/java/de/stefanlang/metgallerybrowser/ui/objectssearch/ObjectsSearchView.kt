package de.stefanlang.metgallerybrowser.ui.objectssearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun ObjectsSearchView(viewModel: ObjectsSearchViewModel) {
    val searchText = viewModel.searchText.collectAsState()
    val search = viewModel.search.collectAsState()
    val isSearching = viewModel.isSearching.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TextField(
            value = searchText.value,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // TODO: Dimension
            placeholder = { Text(text = "Search") } // TODO: localise
        )

        Spacer(modifier = Modifier.height(16.dp)) // TODO: Dimension

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            val objectIDs = search.value.result?.objectIDs
                ?: emptyList() // TODO: empty state, remove train wrecks!
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