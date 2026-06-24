package com.mehtablabs.nitroflix.ui

import android.net.Uri

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object Movies : Screen("movies")
    object WebSeries : Screen("webseries")
    object Sports : Screen("sports")
    object LiveTV : Screen("livetv")
    object Settings : Screen("settings")
    object Player : Screen("player/{streamUrl}") {
        fun createRoute(streamUrl: String): String {
            val encodedUrl = Uri.encode(streamUrl)
            return "player/$encodedUrl"
        }
    }
    object Details : Screen("details/{title}") {
        fun createRoute(title: String) = "details/$title"
    }
}
