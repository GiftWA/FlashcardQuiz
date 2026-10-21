package com.example.myapplication.data

fun getTopics(course: String): List<String> {
    return when (course) {
        "Database Management" -> listOf(
            "Database Fundamentals", "ER Diagrams", "Relational Model", "Normalization"
        )
        "Computer Networks" -> listOf(
            "Network Fundamentals", "OSI Model", "TCP/IP", "Network Security"
        )
        "Operating Systems" -> listOf(
            "Process Management", "Memory Management", "File Systems", "CPU Scheduling"
        )
        "Mobile Application Development" -> listOf(
            "Android Fundamentals", "Activities and Lifecycle", "Jetpack Compose", "Android UI Design"
        )
        else -> emptyList()
    }
}
