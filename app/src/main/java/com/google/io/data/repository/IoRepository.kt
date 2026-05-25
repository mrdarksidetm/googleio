package com.google.io.data.repository

import com.google.io.data.model.Session
import kotlinx.coroutines.flow.Flow

/**
 * An "Interface" is like a contract or a promise.
 * It says: "Whatever repository we use, it MUST be able to do these things."
 * This makes it easy to swap a "Mock" repo for a "Real" one later!
 */
interface IoRepository {
    // A way to get all sessions (from local database)
    fun getSessions(): Flow<List<Session>>

    // A way to get only bookmarked sessions
    fun getBookmarkedSessions(): Flow<List<Session>>

    // A way to search and filter sessions
    fun getFilteredSessions(query: String, category: String): Flow<List<Session>>

    // A way to "refresh" data by fetching from the "internet" (mocked)
    suspend fun refreshSessions()

    // A way to toggle a bookmark
    suspend fun toggleBookmark(sessionId: String, isBookmarked: Boolean)
}
