package de.stefanlang.metgallerybrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.stefanlang.core.domain.Defines
import de.stefanlang.core.domain.navigation.NavRoute
import de.stefanlang.feature.objectssearch.ui.ObjectsSearchView
import de.stefanlang.metgallerybrowser.ui.theme.METGalleryBrowserTheme

@AndroidEntryPoint
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
                            de.stefanlang.feature.objectdetail.ui.ObjectDetailView(
                                navController,
                                objectID
                            )
                        }
                    }
                }
            }
        }
    }
}
