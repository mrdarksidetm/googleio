package com.google.io.data.repository

import com.google.io.data.local.SessionDao
import com.google.io.data.model.Session
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * This is our Fake (Mock) Repository! 
 * It pretends to talk to the internet, but it actually just uses 
 * a list we wrote ourselves.
 *
 * We use "suspend" functions and "delay" to make it feel like 
 * it's really loading from a slow website.
 */
class MockIoRepository(private val sessionDao: SessionDao) : IoRepository {

    override fun getSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    override fun getBookmarkedSessions(): Flow<List<Session>> = sessionDao.getBookmarkedSessions()

    override fun getFilteredSessions(query: String, category: String): Flow<List<Session>> = 
        sessionDao.getFilteredSessions(query, category)

    override suspend fun refreshSessions() {
        // 1. We pretend to wait for the internet for 2 seconds
        delay(2000)

        // 2. Here is our "Hardcoded JSON string" (but represented as a list)
        // In a real app, we would use a library like Retrofit to turn a 
        // real JSON string into this list!
        val mockData = listOf(
            Session(
                id = "1",
                title = "What's new in Android",
                description = "Join us to hear about the latest updates in Android 17 and beyond!",
                speaker = "Dave Burke",
                startTime = "10:00 AM",
                videoUrl = "https://www.youtube.com/watch?v=example1",
                category = "Mobile",
                year = 2026
            ),
            Session(
                id = "2",
                title = "Compose Everywhere",
                description = "Learn how to use Jetpack Compose for everything from watches to cars.",
                speaker = "Anna-Chiara Bellini",
                startTime = "11:30 AM",
                videoUrl = "https://www.youtube.com/watch?v=example2",
                category = "Mobile",
                year = 2026
            ),
            Session(
                id = "3",
                title = "The Future of Gemini",
                description = "Exploring how AI is transforming the way we build apps.",
                speaker = "Sundar Pichai",
                startTime = "1:00 PM",
                videoUrl = "https://www.youtube.com/watch?v=example3",
                category = "AI",
                year = 2026
            ),
            Session(
                id = "4",
                title = "Retro: Android 1.0",
                description = "A trip down memory lane to look at where it all started.",
                speaker = "Reto Meier",
                startTime = "3:00 PM",
                videoUrl = "https://www.youtube.com/watch?v=example4",
                category = "History",
                year = 2008
            )
        )
        // 3. We save our new data into the Room Database!
        // This is the "Offline-First" part: even if the internet goes away later, 
        // the app will still show this data because it's now in the database.
        sessionDao.insertSessions(mockData)
    }

    override suspend fun toggleBookmark(sessionId: String, isBookmarked: Boolean) {
        // We tell the database to update the "star" status for this session
        sessionDao.updateBookmark(sessionId, isBookmarked)
    }
}
