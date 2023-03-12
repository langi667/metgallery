package de.stefanlang.metgallerybrowser.ui.navigation

import androidx.navigation.NavController

object NavUtil {
    fun navigateToObjectDetail(navController: NavController, objectID: Int){
        val route = NavRoute.ObjectDetail.route.replace("{objectID}", "$objectID")
        navController.navigate(route)
    }
}