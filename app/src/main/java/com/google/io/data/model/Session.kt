package com.google.io.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is our "Session" data class. 
 * Imagine this as a digital ticket or a card that holds all the information 
 * about a specific talk or event at Google I/O.
 *
 * We use @Entity to tell "Room" (our local database) that we want to save 
 * these sessions on the phone so they work even without internet!
 */
@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey val id: String, // A unique ID so we don't get sessions mixed up
    val title: String,        // The name of the talk (e.g., "What's new in Compose")
    val description: String,  // A short story about what the talk is about
    val speaker: String,      // The person who is giving the talk
    val startTime: String,    // When the talk starts
    val videoUrl: String,     // A link to the video so we can watch it later
    val imageUrl: String = "https://picsum.photos/seed/${id}/400/200", // A beautiful image for the session
    val category: String = "Mobile", // Category like "AI", "Mobile", "Cloud"
    val isBookmarked: Boolean = false, // A "Star" button status to save for later
    val year: Int             // The year of the event (e.g., 2026 or 2024)
)
