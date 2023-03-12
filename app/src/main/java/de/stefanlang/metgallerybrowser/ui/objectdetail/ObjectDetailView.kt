package de.stefanlang.metgallerybrowser.ui.objectdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import de.stefanlang.metgallerybrowser.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObjectDetailView(navController: NavController, objectID: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    )

    Scaffold(topBar = {
        TopBar(navController)
    }) {
        ContentView(navController, objectID)
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
                    contentDescription = "Back"
                )
            }
        }
    )
}

private fun ContentView(navController: NavController, objectID: Int) {

}