package de.stefanlang.metgallerybrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.stefanlang.metgallerybrowser.domain.Defines
import de.stefanlang.metgallerybrowser.ui.navigation.NavRoute
import de.stefanlang.metgallerybrowser.ui.objectdetail.ObjectDetailView
import de.stefanlang.metgallerybrowser.ui.objectssearch.ObjectsSearchView
import de.stefanlang.metgallerybrowser.ui.theme.METGalleryBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            METGalleryBrowserTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavRoute.ObjectsSearch.route
                    ) {
                        composable(NavRoute.ObjectsSearch.route) {
                            ObjectsSearchView(navController)
                        }
                        composable(
                            route = NavRoute.ObjectDetail.route,
                            arguments = NavRoute.ObjectDetail.navArgs
                        ) {
                            val objectID = it.arguments?.getInt("objectID") ?: Defines.InvalidID
                            ObjectDetailView(navController, objectID)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    METGalleryBrowserTheme {
        Greeting("Android")
    }
}