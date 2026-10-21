package com.example.myapplication.data

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

fun getQuizQuestions(topic: String): List<QuizQuestion> {
    return when (topic) {
        "Database Fundamentals" -> listOf(
            QuizQuestion("What is a primary key?", listOf("A key that uniquely identifies each row", "A key used only for sorting", "A key used to delete a database", "A key used to connect to Wi-Fi"), "A key that uniquely identifies each row"),
            QuizQuestion("What does DBMS stand for?", listOf("Data Backup Management System", "Database Management System", "Digital Browser Management Software", "Database Memory Storage"), "Database Management System"),
            QuizQuestion("What is SQL used for?", listOf("Designing mobile app screens", "Connecting to Wi-Fi networks", "Creating and managing data in a database", "Editing photos"), "Creating and managing data in a database"),
            QuizQuestion("What is a database table made of?", listOf("Folders and files", "Pages and frames", "Nodes and routers", "Rows and columns"), "Rows and columns")
        )
        "ER Diagrams" -> listOf(
            QuizQuestion("What does an ER diagram show?", listOf("Entities and the relationships between them", "The speed of a network", "The steps of a program", "The layers of an operating system"), "Entities and the relationships between them"),
            QuizQuestion("In an ER diagram, what is an entity?", listOf("A type of network cable", "A thing about which data is stored", "A programming language", "A kind of database password"), "A thing about which data is stored"),
            QuizQuestion("What is an attribute?", listOf("A link between two networks", "A type of file system", "A property that describes an entity", "A database backup"), "A property that describes an entity"),
            QuizQuestion("What does cardinality describe?", listOf("How fast a query runs", "How big a hard disk is", "How many users can log in", "How many instances of one entity relate to another"), "How many instances of one entity relate to another")
        )
        "Relational Model" -> listOf(
            QuizQuestion("What is a tuple?", listOf("A single row in a table", "A single column in a table", "A database password", "A type of network"), "A single row in a table"),
            QuizQuestion("What is a relation in the relational model?", listOf("A link between two computers", "A table of rows and columns", "A type of programming loop", "A user account"), "A table of rows and columns"),
            QuizQuestion("What is a foreign key?", listOf("A key that deletes a table", "A key used to encrypt Wi-Fi", "A column that refers to the primary key of another table", "A key that sorts data alphabetically"), "A column that refers to the primary key of another table"),
            QuizQuestion("What is a database schema?", listOf("A backup of a database", "A type of query", "A database user", "The structure of a database"), "The structure of a database")
        )
        "Normalization" -> listOf(
            QuizQuestion("What is the main purpose of normalization?", listOf("To reduce data redundancy", "To make a database larger", "To speed up the internet", "To encrypt data"), "To reduce data redundancy"),
            QuizQuestion("What does First Normal Form (1NF) require?", listOf("Every table has exactly three columns", "Each column holds a single value", "No table has a primary key", "All data is stored in one table"), "Each column holds a single value"),
            QuizQuestion("What does Second Normal Form (2NF) require?", listOf("Every table has two rows", "No table has a foreign key", "Every non-key column depends on the whole primary key", "All columns are numbers"), "Every non-key column depends on the whole primary key"),
            QuizQuestion("What does Third Normal Form (3NF) require?", listOf("Every table has three columns", "All data is stored in one column", "Every table has three keys", "No non-key column depends on another non-key column"), "No non-key column depends on another non-key column")
        )
        "Network Fundamentals" -> listOf(
            QuizQuestion("What does a router do?", listOf("Forwards data between networks", "Stores web pages", "Prints documents", "Cools the computer"), "Forwards data between networks"),
            QuizQuestion("What is an IP address?", listOf("A type of cable", "A unique number that identifies a device on a network", "A web browser", "A password for Wi-Fi"), "A unique number that identifies a device on a network"),
            QuizQuestion("What area does a LAN usually cover?", listOf("An entire country", "Only one computer", "A small area such as a building", "The whole world"), "A small area such as a building"),
            QuizQuestion("What is a network?", listOf("A single computer with a screen", "A type of software license", "A kind of keyboard", "Two or more devices connected to share data"), "Two or more devices connected to share data")
        )
        "OSI Model" -> listOf(
            QuizQuestion("How many layers does the OSI model have?", listOf("Seven", "Four", "Five", "Nine"), "Seven"),
            QuizQuestion("Which layer is responsible for routing?", listOf("Physical layer", "Network layer", "Application layer", "Session layer"), "Network layer"),
            QuizQuestion("Which layer provides end-to-end delivery of data?", listOf("Presentation layer", "Data Link layer", "Transport layer", "Physical layer"), "Transport layer"),
            QuizQuestion("Which layer deals with cables and electrical signals?", listOf("Application layer", "Transport layer", "Session layer", "Physical layer"), "Physical layer")
        )
        "TCP/IP" -> listOf(
            QuizQuestion("What does TCP provide?", listOf("Reliable, ordered delivery of data", "Faster delivery without any checking", "Screen display", "Image editing"), "Reliable, ordered delivery of data"),
            QuizQuestion("What does IP do?", listOf("Stores emails", "Addresses and routes packets to their destination", "Draws web pages", "Compresses images"), "Addresses and routes packets to their destination"),
            QuizQuestion("How does UDP compare with TCP?", listOf("It is slower and more reliable", "It is used only for printers", "It is faster but does not guarantee delivery", "It is used only for databases"), "It is faster but does not guarantee delivery"),
            QuizQuestion("What does DNS do?", listOf("Blocks viruses", "Stores passwords", "Sends emails", "Translates domain names into IP addresses"), "Translates domain names into IP addresses")
        )
        "Network Security" -> listOf(
            QuizQuestion("What does a firewall do?", listOf("Filters network traffic using security rules", "Speeds up downloads", "Stores backups", "Creates new IP addresses"), "Filters network traffic using security rules"),
            QuizQuestion("What is encryption?", listOf("Deleting old files", "Converting data into a coded form", "Making a copy of data", "Increasing network speed"), "Converting data into a coded form"),
            QuizQuestion("What is phishing?", listOf("Fixing a network fault", "A way to speed up Wi-Fi", "Tricking people into revealing sensitive information", "A type of backup"), "Tricking people into revealing sensitive information"),
            QuizQuestion("What does HTTPS do?", listOf("Makes websites load images faster", "Stores cookies permanently", "Blocks all advertisements", "Encrypts communication between a browser and a website"), "Encrypts communication between a browser and a website")
        )
        "Process Management" -> listOf(
            QuizQuestion("What is a process?", listOf("A program that is currently running", "A file stored on disk", "A type of hardware", "A network cable"), "A program that is currently running"),
            QuizQuestion("What does a Process Control Block (PCB) store?", listOf("The user's passwords", "Information about a process, such as its ID and state", "The list of all files on the disk", "The computer's IP address"), "Information about a process, such as its ID and state"),
            QuizQuestion("Which of these is a common process state?", listOf("Typing", "Printing", "Running", "Browsing"), "Running"),
            QuizQuestion("What is context switching?", listOf("Changing the screen brightness", "Switching a computer off", "Copying a file to a USB drive", "Saving one process's state and loading another's"), "Saving one process's state and loading another's")
        )
        "Memory Management" -> listOf(
            QuizQuestion("What is paging?", listOf("Dividing memory into fixed-size blocks", "Deleting unused programs", "Increasing screen size", "Connecting to the internet"), "Dividing memory into fixed-size blocks"),
            QuizQuestion("What is virtual memory?", listOf("A faster type of CPU", "Disk space used to extend RAM", "A kind of monitor", "Memory inside the keyboard"), "Disk space used to extend RAM"),
            QuizQuestion("What is a page fault?", listOf("A broken page on a website", "An error in the keyboard", "Accessing a page that is not currently in RAM", "A full hard disk"), "Accessing a page that is not currently in RAM"),
            QuizQuestion("What is fragmentation?", listOf("Extra memory added to RAM", "A way to compress files", "A type of virus", "Wasted memory split into small unusable pieces"), "Wasted memory split into small unusable pieces")
        )
        "File Systems" -> listOf(
            QuizQuestion("What is the purpose of a file system?", listOf("To store, organise and retrieve files", "To draw graphics on the screen", "To connect to the internet", "To cool the CPU"), "To store, organise and retrieve files"),
            QuizQuestion("What is a directory?", listOf("A type of virus", "A folder that holds files and other folders", "A kind of processor", "A network protocol"), "A folder that holds files and other folders"),
            QuizQuestion("Which of these is an example of a file system?", listOf("HTTP", "HTML", "NTFS", "Bluetooth"), "NTFS"),
            QuizQuestion("What do file permissions do?", listOf("Decide the colour of a file", "Increase the size of a file", "Speed up the CPU", "Control who can read, write or execute a file"), "Control who can read, write or execute a file")
        )
        "CPU Scheduling" -> listOf(
            QuizQuestion("What does CPU scheduling decide?", listOf("Which process gets the CPU next", "Which website to open", "How much a computer costs", "Which file to delete"), "Which process gets the CPU next"),
            QuizQuestion("How does First Come First Served (FCFS) work?", listOf("The shortest process always runs first", "Processes run in the order they arrive", "Processes run in random order", "The largest process always runs first"), "Processes run in the order they arrive"),
            QuizQuestion("How does Round Robin scheduling work?", listOf("Only the longest process runs", "Processes are never switched", "Each process gets a fixed time slice in turn", "Only the newest process runs"), "Each process gets a fixed time slice in turn"),
            QuizQuestion("What is turnaround time?", listOf("The time the CPU is idle", "The time a computer takes to start", "The time taken to type a command", "The total time from submission to completion"), "The total time from submission to completion")
        )
        "Android Fundamentals" -> listOf(
            QuizQuestion("Which programming language is recommended for Android development?", listOf("Kotlin", "COBOL", "Fortran", "Pascal"), "Kotlin"),
            QuizQuestion("What is an APK?", listOf("A type of phone battery", "A file used to install an Android app", "A programming language", "A kind of emulator"), "A file used to install an Android app"),
            QuizQuestion("What does the AndroidManifest.xml file do?", listOf("Stores the user's photos", "Compiles Kotlin code", "Declares the app's components and permissions", "Charges the phone battery"), "Declares the app's components and permissions"),
            QuizQuestion("What is an Activity?", listOf("A phone battery setting", "A type of file system", "A network cable", "A single screen with a user interface"), "A single screen with a user interface")
        )
        "Activities and Lifecycle" -> listOf(
            QuizQuestion("What is the activity lifecycle?", listOf("The states an activity moves through from creation to destruction", "How long a phone battery lasts", "How many apps are installed", "The warranty period of a phone"), "The states an activity moves through from creation to destruction"),
            QuizQuestion("Which method is called when an activity is first created?", listOf("onDestroy()", "onCreate()", "onStop()", "onPause()"), "onCreate()"),
            QuizQuestion("Which method is called when an activity becomes visible to the user?", listOf("onDestroy()", "onStop()", "onStart()", "onPause()"), "onStart()"),
            QuizQuestion("What does onDestroy() do?", listOf("It is called when the activity first appears", "It is called when the user taps a button", "It is called when the app is installed", "It is called before the activity is destroyed, to clean up resources"), "It is called before the activity is destroyed, to clean up resources")
        )
        "Jetpack Compose" -> listOf(
            QuizQuestion("What is Jetpack Compose?", listOf("Android's modern toolkit for building user interfaces with Kotlin", "A database system", "A web browser", "An image editor"), "Android's modern toolkit for building user interfaces with Kotlin"),
            QuizQuestion("What does @Composable mean?", listOf("It deletes a function", "It marks a function that describes part of the user interface", "It connects the app to the internet", "It creates a database table"), "It marks a function that describes part of the user interface"),
            QuizQuestion("What is state in Compose?", listOf("A country in an address", "A permanent file", "A value that can change and updates the screen", "A hardware component"), "A value that can change and updates the screen"),
            QuizQuestion("What does remember do?", listOf("It saves a file to disk permanently", "It sends a message to the user", "It closes the app", "It keeps a value when the screen is redrawn"), "It keeps a value when the screen is redrawn")
        )
        "Android UI Design" -> listOf(
            QuizQuestion("What is Material Design?", listOf("Google's design system with guidelines and components", "A type of phone case", "A database tool", "A programming language"), "Google's design system with guidelines and components"),
            QuizQuestion("Why do we use dp for sizes?", listOf("It measures battery life", "It keeps sizes consistent across different screens", "It is a colour code", "It measures internet speed"), "It keeps sizes consistent across different screens"),
            QuizQuestion("What does a Column do in Compose?", listOf("It places items side by side", "It deletes items", "It places items vertically, one after another", "It draws a circle"), "It places items vertically, one after another"),
            QuizQuestion("Why is consistent spacing important?", listOf("It makes the app larger in storage", "It connects to the internet faster", "It hides the buttons", "It makes the interface neat and easy to read"), "It makes the interface neat and easy to read")
        )
        else -> listOf(
            QuizQuestion("No quiz questions available.", listOf("Please choose another topic."), "Please choose another topic.")
        )
    }
}
