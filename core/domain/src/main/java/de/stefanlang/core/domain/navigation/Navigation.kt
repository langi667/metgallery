package de.stefanlang.core.domain.navigation

import androidx.navigation.NavController

fun NavController.navigateToObjectDetail(navController: NavController, objectID: Int) {
    val route = NavRoute.ObjectDetail.route.replace("{objectID}", "$objectID")
    navController.navigate(route)
}