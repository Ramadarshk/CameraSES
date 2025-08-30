# CameraSES

Author: Kotha Ram Adarsh
Email: [adarsh.3545@gmail.com](mailto:adarsh.3545@gmail.com)

A mobile app for capturing session-wise images, storing metadata, and searching by session. Built using Android (Kotlin), CameraX, Room (SQLite), Jetpack Compose and MVVM architecture.

---

## Features

* *Session-Based Image Capture*
  Capture multiple images in a single session using the phone’s camera.

* *Metadata Entry*
  At the end of each session, enter:

  * Name
  * Age
  * Session ID (auto-generated: Name_Age_Timestamp)

* *Local Storage*

  * *Images:* Stored in app-specific directory
    Android/media/cameraSES/sessions/<SessionID>/IMG_timestamp.jpg
  * *Metadata:* Stored in SQLite using Room

* *Search Functionality*
  Search sessions by SessionID or Name. Click a session to view all images and metadata.

* *Architecture & Libraries*

  * MVVM architecture
  * CameraX for camera functionality
  * Room for local database
  * Jetpack Compose for UI
  * Kotlin coroutines and Flow for reactive updates

---

## Project Structure


max.project.camerases/
├── dataBase/
│   ├── SessionDAO.kt
│   ├── SessionDB.kt
│   └── Sessions.kt
├── dataclasses/
│   └── data.kt
├── Nav/
│   └── NavCollect.kt
├── ui.theme/
├── util/
│   ├── CameraPreview.kt
│   └── Sheets.kt
├── viewmodels/
│   ├── CameraView.kt
│   └── SessionViews.kt
└── MainActivity.kt


*Description:*

* dataBase/ - Room database entities and DAO
* dataclasses/ - Data classes for session and photo info
* Nav/ – Navigation routes
* ui.theme/ - Compose theme
* util/ – Reusable Compose components
* viewmodels/ – ViewModels for camera and session management
* MainActivity.kt – App entry point and navigation host

---

## How It Works

1. *Create Session*

   * Starts a session and opens camera preview
   * Captured images are stored in memory

2. *End Session*

   * User enters Name and Age
   * SessionID is generated: Name_Age_Timestamp
   * Photos saved to app storage and metadata stored in Room

3. *Search & View*

   * Navigate to search screen
   * Enter session name or partial ID to find sessions
   * Click a session to view metadata and photos

---

## Demo

A screen recording of the app in use has been uploaded to the **videos/ folder**.
This demo shows how to create a session, capture photos, save metadata, and search/view sessions.

---

## Installation & Setup

1. *Clone the repository*

   bash
   git clone <your-repo-url>
   cd cameraSES
   

2. *Open in Android Studio*

   * Select Open an Existing Project
   * Sync Gradle and build the project

3. *Run on a device/emulator*

   * Grant camera and storage permissions

4. *Start Using*

   * Click Create Session → Capture photos → Enter name and age → Save
   * View sessions → Search by session ID or name

---

## Contact

Adarsh Ramadarsh
Email: [adarsh.3545@gmail.com](mailto:adarsh.3545@gmail.com)
