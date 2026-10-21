package com.example.myapplication.data

data class Flashcard(val question: String, val answer: String)

fun getFlashcards(topic: String): List<Flashcard> {
    return when (topic) {
        "Database Fundamentals" -> listOf(
            Flashcard("What is a database?", "An organised collection of data that can be easily stored, accessed and managed."),
            Flashcard("What is a DBMS?", "A Database Management System, the software used to create, manage and control access to databases."),
            Flashcard("What is a primary key?", "A column (or set of columns) that uniquely identifies each row in a table."),
            Flashcard("What is SQL?", "Structured Query Language, used to create, read, update and delete data in a relational database.")
        )
        "ER Diagrams" -> listOf(
            Flashcard("What is an ER diagram?", "A diagram that shows the entities in a database and the relationships between them."),
            Flashcard("What is an entity?", "A thing about which data is stored, such as Student or Course."),
            Flashcard("What is an attribute?", "A property that describes an entity, such as a student's name."),
            Flashcard("What is cardinality?", "It describes how many instances of one entity can relate to another: one-to-one, one-to-many or many-to-many.")
        )
        "Relational Model" -> listOf(
            Flashcard("What is a relation?", "A table made up of rows and columns."),
            Flashcard("What is a tuple?", "A single row in a relation."),
            Flashcard("What is a foreign key?", "A column that refers to the primary key of another table in order to link them."),
            Flashcard("What is a schema?", "The structure of a database, including its tables, columns and data types.")
        )
        "Normalization" -> listOf(
            Flashcard("What is normalization?", "Organising data into tables to reduce redundancy and improve data integrity."),
            Flashcard("What does First Normal Form (1NF) require?", "Each column holds a single value and there are no repeating groups."),
            Flashcard("What does Second Normal Form (2NF) require?", "The table is in 1NF and every non-key column depends on the whole primary key."),
            Flashcard("What does Third Normal Form (3NF) require?", "The table is in 2NF and no non-key column depends on another non-key column.")
        )
        "Network Fundamentals" -> listOf(
            Flashcard("What is a network?", "Two or more devices connected together to share data and resources."),
            Flashcard("What is a router?", "A device that forwards data between different networks."),
            Flashcard("What is an IP address?", "A unique number that identifies a device on a network."),
            Flashcard("What is the difference between a LAN and a WAN?", "A LAN covers a small area such as a building, while a WAN covers a large area such as a country.")
        )
        "OSI Model" -> listOf(
            Flashcard("How many layers does the OSI model have?", "Seven."),
            Flashcard("Name the seven OSI layers.", "Physical, Data Link, Network, Transport, Session, Presentation and Application."),
            Flashcard("Which layer is responsible for routing?", "The Network layer."),
            Flashcard("Which layer provides end-to-end delivery of data?", "The Transport layer.")
        )
        "TCP/IP" -> listOf(
            Flashcard("What does TCP do?", "It provides reliable, ordered delivery of data between devices."),
            Flashcard("What does IP do?", "It addresses and routes packets so they reach their destination."),
            Flashcard("What is the difference between TCP and UDP?", "TCP is reliable and connection-based, while UDP is faster but does not guarantee delivery."),
            Flashcard("What is DNS?", "The Domain Name System, which translates names like google.com into IP addresses.")
        )
        "Network Security" -> listOf(
            Flashcard("What is a firewall?", "A system that filters network traffic based on security rules."),
            Flashcard("What is encryption?", "Converting data into a coded form so only authorised people can read it."),
            Flashcard("What is phishing?", "Tricking people into giving away sensitive information using fake emails or websites."),
            Flashcard("What does HTTPS do?", "It encrypts the communication between a browser and a website.")
        )
        "Process Management" -> listOf(
            Flashcard("What is a process?", "A program that is currently running."),
            Flashcard("What is a Process Control Block (PCB)?", "A data structure that stores information about a process, such as its ID and state."),
            Flashcard("Name the common process states.", "New, Ready, Running, Waiting and Terminated."),
            Flashcard("What is context switching?", "Saving the state of one process and loading another so the CPU can switch between them.")
        )
        "Memory Management" -> listOf(
            Flashcard("What is paging?", "Dividing memory into fixed-size blocks called pages and frames to use memory efficiently."),
            Flashcard("What is virtual memory?", "A technique that uses disk space to extend RAM so programs can be larger than physical memory."),
            Flashcard("What is a page fault?", "It happens when a program accesses a page that is not currently in RAM."),
            Flashcard("What is fragmentation?", "Wasted memory that is split into small pieces that are too small to use.")
        )
        "File Systems" -> listOf(
            Flashcard("What is a file system?", "The method an operating system uses to store, organise and retrieve files on a storage device."),
            Flashcard("What is a directory?", "A folder that holds files and other directories."),
            Flashcard("Give three examples of file systems.", "NTFS, FAT32 and ext4."),
            Flashcard("What are file permissions?", "Rules that control who can read, write or execute a file.")
        )
        "CPU Scheduling" -> listOf(
            Flashcard("What is CPU scheduling?", "Deciding which process in the ready queue gets the CPU next."),
            Flashcard("How does First Come First Served (FCFS) work?", "Processes run in the order in which they arrive."),
            Flashcard("How does Round Robin scheduling work?", "Each process gets a fixed time slice, one after another."),
            Flashcard("What is turnaround time?", "The total time from when a process is submitted until it completes.")
        )
        "Android Fundamentals" -> listOf(
            Flashcard("Which programming language is recommended for Android development?", "Kotlin."),
            Flashcard("What is an APK?", "An Android Package file used to install an app on a device."),
            Flashcard("What does the AndroidManifest.xml file do?", "It declares the app's components, permissions and basic information."),
            Flashcard("What is an Activity?", "A single screen of an app that has a user interface.")
        )
        "Activities and Lifecycle" -> listOf(
            Flashcard("What is the activity lifecycle?", "The set of states an activity moves through from creation to destruction."),
            Flashcard("Which method is called when an activity is first created?", "onCreate()."),
            Flashcard("Which method is called when an activity becomes visible to the user?", "onStart()."),
            Flashcard("What does onDestroy() do?", "It is called before the activity is destroyed, so resources can be cleaned up.")
        )
        "Jetpack Compose" -> listOf(
            Flashcard("What is Jetpack Compose?", "Android's modern toolkit for building native user interfaces using Kotlin functions."),
            Flashcard("What does @Composable mean?", "It marks a function that describes part of the user interface."),
            Flashcard("What is state in Compose?", "A value that can change and causes the screen to update when it changes."),
            Flashcard("What does remember do?", "It keeps a value so it is not lost when the screen is redrawn.")
        )
        "Android UI Design" -> listOf(
            Flashcard("What is Material Design?", "Google's design system with guidelines and components for building consistent apps."),
            Flashcard("Why do we use dp for sizes?", "Density-independent pixels keep sizes consistent across different screens."),
            Flashcard("What does a Column do in Compose?", "It places its children vertically, one after another."),
            Flashcard("Why is consistent spacing important?", "It makes the interface neat, balanced and easy to read.")
        )
        else -> listOf(
            Flashcard("No flashcards available.", "Please choose another topic.")
        )
    }
}
