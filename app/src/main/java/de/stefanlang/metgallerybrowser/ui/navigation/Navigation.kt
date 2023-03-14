package de.stefanlang.metgallerybrowser.ui.navigation

import androidx.navigation.NavController

object Navigation {

    // region Public API

    fun NavController.navigateToObjectDetail(navController: NavController, objectID: Int) {
        val route = NavRoute.ObjectDetail.route.replace("{objectID}", "$objectID")
        navController.navigate(route)
    }

    // endregion
}