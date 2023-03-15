package de.stefanlang.metgallerybrowser.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

// region Types

enum class NavRoute(val route: String, val navArgs: List<NamedNavArgument> = emptyList()) {
    ObjectsSearch("objectsSearch"),

    ObjectDetail(
        "objectDetail/{objectID}",
        navArgs = listOf(navArgument("objectID") {
            type = NavType.IntType
        })
    ),
}

// endregion
