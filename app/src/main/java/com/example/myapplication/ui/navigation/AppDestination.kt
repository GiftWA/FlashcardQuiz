package com.example.myapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.ui.graphics.vector.ImageVector

// The 5 bottom-nav destinations, matching the Stitch mockups.
enum class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Outlined.Home),
    STUDY("study", "Study", Icons.Outlined.MenuBook),
    QUIZZES("quizzes", "Quizzes", Icons.Outlined.Quiz),
    SAVED("saved", "Saved", Icons.Outlined.BookmarkBorder),
    PROFILE("profile", "Profile", Icons.Outlined.Person)
}