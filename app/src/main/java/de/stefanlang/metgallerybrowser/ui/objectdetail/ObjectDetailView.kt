package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.R
import de.stefanlang.metgallerybrowser.ui.common.ErrorStateHint
import de.stefanlang.metgallerybrowser.ui.common.LoadingStateHint

// region Public API

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectDetailView(navController: NavController, objectID: Int) {
    val viewModel: ObjectDetailViewModel = viewModel()

    val state = viewModel.state.collectAsState()
    viewModel.loadObjectForID(objectID)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    )

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
    when(state) {
        is ObjectDetailViewModel.State.Loading -> {
            LoadingStateHint()
        }

        is ObjectDetailViewModel.State.FinishedWithError -> {
            ErrorStateHint()
        }
        // TODO: implement
        is ObjectDetailViewModel.State.FinishedWithSuccess -> {
            Text("Success")
        }
    }
}

// endregion